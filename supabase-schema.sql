-- Daily Kitchen & Pub: shared online ordering database
-- Run this in Supabase SQL Editor after creating your project.

create extension if not exists pgcrypto;

create table if not exists public.orders (
  id uuid primary key default gen_random_uuid(),
  order_code text unique not null,
  customer_id uuid references auth.users(id) on delete set null,
  customer_name text not null,
  customer_phone text not null,
  fulfillment_type text not null check (fulfillment_type in ('pickup','delivery')),
  delivery_address text,
  order_notes text,
  total_ghs numeric(12,2) not null check (total_ghs >= 0),
  status text not null default 'Confirmed'
    check (status in ('Confirmed','Payment Confirmed','Preparing','Ready/Delivered','Cancelled')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.order_items (
  id bigint generated always as identity primary key,
  order_id uuid not null references public.orders(id) on delete cascade,
  item_name text not null,
  unit_price_ghs numeric(12,2) not null check (unit_price_ghs >= 0),
  quantity integer not null check (quantity > 0),
  line_total_ghs numeric(12,2) not null check (line_total_ghs >= 0)
);

create index if not exists orders_created_at_idx on public.orders(created_at desc);
create index if not exists orders_customer_id_idx on public.orders(customer_id);
create index if not exists order_items_order_id_idx on public.order_items(order_id);

alter table public.orders enable row level security;
alter table public.order_items enable row level security;

-- Customers can create orders without signing in.
drop policy if exists "public can create orders" on public.orders;
create policy "public can create orders"
on public.orders for insert
to anon, authenticated
with check (true);

drop policy if exists "public can create order items" on public.order_items;
create policy "public can create order items"
on public.order_items for insert
to anon, authenticated
with check (true);

-- Only signed-in staff/admin users can read/update all orders.
-- Set user_metadata/app_metadata role to 'staff' or 'admin' for staff accounts.
drop policy if exists "staff can read orders" on public.orders;
create policy "staff can read orders"
on public.orders for select
to authenticated
using (auth.uid() = '06cab660-af20-49aa-abc4-4e0f2211826a'::uuid);

drop policy if exists "staff can update orders" on public.orders;
create policy "staff can update orders"
on public.orders for update
to authenticated
using (auth.uid() = '06cab660-af20-49aa-abc4-4e0f2211826a'::uuid)
with check (auth.uid() = '06cab660-af20-49aa-abc4-4e0f2211826a'::uuid);

drop policy if exists "staff can read order items" on public.order_items;
create policy "staff can read order items"
on public.order_items for select
to authenticated
using (
  exists (
    select 1 from public.orders o
    where o.id = order_items.order_id
      and auth.uid() = '06cab660-af20-49aa-abc4-4e0f2211826a'::uuid
  )
);

-- Keep updated_at current.
create or replace function public.set_orders_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

drop trigger if exists orders_updated_at on public.orders;
create trigger orders_updated_at
before update on public.orders
for each row execute function public.set_orders_updated_at();

-- IMPORTANT:
-- Do not put a Supabase secret/service_role key in the website.
-- The website should use the project's publishable/anon client key with RLS.
