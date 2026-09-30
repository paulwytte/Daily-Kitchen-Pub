package com.dailykitchenpub.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import android.graphics.BitmapFactory;
import android.content.SharedPreferences;

public class MainActivity extends Activity {
    static final String WHATSAPP = "233241936496";
    static final String PHONE = "0241936496";
    static final String WEBSITE = "https://paulwytte.github.io/Daily-Kitchen-Pub/";
    static final int GOLD = Color.rgb(218,165,32);
    static final int BLACK = Color.BLACK;
    static final int DARK = Color.rgb(18,18,18);
    LinearLayout content, cartList;
    TextView totalView, cartBadge;
    final LinkedHashMap<Item, Integer> cart = new LinkedHashMap<>();
    EditText customerName, customerPhone, deliveryAddress, orderNotes;
    RadioButton pickup, delivery;
    SharedPreferences ordersPrefs;
    ExecutorService executor = Executors.newFixedThreadPool(3);

    static class Item {
        String name, category, priceLabel; int price;
        Item(String n, String c, int p){name=n;category=c;price=p;priceLabel="GHS "+p;}
        Item(String n, String c, int p, String label){name=n;category=c;price=p;priceLabel=label;}
        public boolean equals(Object o){return o instanceof Item && name.equals(((Item)o).name);}
        public int hashCode(){return name.hashCode();}
    }

    final String[][] FOOD = {
        {"Local Dishes","Banku & Okro with Tilapia","70"},{"Local Dishes","Banku with Tilapia Pepper","80"},
        {"Local Dishes","Banku with Grilled Tilapia","80"},{"Local Dishes","Banku with Salmon Pepper","80"},
        {"Local Dishes","Akple with Ademey","80"},{"Local Dishes","Fufu with Goat Soup","160"},
        {"Local Dishes","Omotuo with Groundnut Soup","100"},{"Local Dishes","Aborbitadi","70"},
        {"Additional Local","Banku with Ademey","70"},{"Additional Local","Ewokple with Aborbitadi","160"},
        {"Additional Local","Ewokple with Salmon Pepper","80"},{"Rice Dishes","Jollof Rice with Goat Meat","120"},
        {"Rice Dishes","Jollof Rice with Cow Meat","130"},{"Rice Dishes","Assorted Jollof Rice Special","150"},
        {"Rice Dishes","Assorted Fried Rice Special","150"},{"Rice Dishes","Plain Rice with Egg Stew","130"},
        {"Rice Dishes","Plain Rice with Egg Stew & Goat Meat","160"},{"Rice Dishes","Plain Rice with Egg Stew & Cow Meat","160"},
        {"Yam & Pasta","Boiled Yam with Egg Stew","100"},{"Yam & Pasta","Fried Yam with Pork (Optional)","130"},
        {"Yam & Pasta","Assorted Spaghetti","130"},{"Grills & Sides","Turkey","130"},
        {"Grills & Sides","Pork","130"},{"Grills & Sides","Chicken Wings","90"},{"Grills & Sides","Salad","40"},
        {"Soups","Goat Soup","100"},{"Soups","Chicken Soup","80"},{"Soups","Groundnut Soup","100"},{"Soups","Palm Nut Soup","80"},
        {"Saturday Special","Atieke, Fried Plantains & Tilapia","120"},{"Natural Fruit Drinks","Fresh Juice","30"},
        {"Natural Fruit Drinks","Hibiscus Drink (Zobo)","25"},{"Natural Fruit Drinks","Tropical Juice","30"},
        {"Natural Fruit Drinks","Pineapple Juice","30"}
    };

    final String[][] DRINKS = {
        {"Soft Drinks","Malt","15"},{"Soft Drinks","B.B Cocktail","15"},{"Soft Drinks","Alvaro","15"},{"Soft Drinks","Sprite","15"},
        {"Soft Drinks","Fanta","15"},{"Soft Drinks","Uni Fresh","20"},{"Soft Drinks","Ceres","80"},{"Soft Drinks","Cranberry","80"},
        {"Soft Drinks","Don Simond Cocktail","70"},{"Soft Drinks","Fru Telli","70"},{"Soft Drinks","1 litre Coke","50"},
        {"Soft Drinks","Plastic Medium Coke","15"},{"Soft Drinks","Water 500ml","5"},{"Soft Drinks","Water 750ml","10"},
        {"Beer","Guinness","15"},{"Beer","Mini Club","15"},{"Beer","Club Shandy","15"},{"Beer","Heineken Bottle","30"},
        {"Beer","Stella","30"},{"Beer","Budweiser","30"},{"Beer","Heineken Can","25"},{"Beer","Orijin","25"},
        {"Beer","Guilder","20"},{"Beer","Large Club","20"},{"Beer","Star","25"},{"Beer","Eagle","20"},
        {"Ciders & Energy","Smirnoff Ice","25","GHS 25 / 500"},{"Ciders & Energy","Hunters Gold","25","GHS 25 / 500"},
        {"Ciders & Energy","Savanna Dry","25","GHS 25 / 500"},{"Ciders & Energy","Kiss","30"},{"Ciders & Energy","Rox","30"},
        {"Ciders & Energy","Red Bull","30"},{"Ciders & Energy","Vody","25"},{"Ciders & Energy","Black Bullet","25"},
        {"Whisky","Johnnie Walker Red Label","120"},{"Whisky","Black Label","180"},{"Whisky","Jack Daniel’s","150"},
        {"Whisky","Jameson","150"},{"Whisky","Chivas Regal","200"},{"Whisky","Ballantine’s","120"},
        {"Gin","Beefeater","25","GHS 25 / 500"},{"Gin","Gordon","25","GHS 25 / 500"},{"Gin","Gordon’s","100"},
        {"Gin","Castle Bridge","6"},{"Gin","Alamo Black","6"},{"Gin","Mandingo","6"},{"Gin","Herbafrik","6"},
        {"Mixers","Tonic Water","15"},{"Mixers","Soda Water","10"},{"Mixers","Lemonade","10"},{"Mixers","Ginger Ale","10"},
        {"Mixers","Orange Juice","10"},{"Mixers","Pineapple Juice","10"},{"Mixers","Cranberry Juice","10"},
        {"Wine","Condor Peak","250"},{"Wine","Dragon’s Back","250"},{"Wine","Four Special","250"},
        {"Wine","Scavi And Ray","200"},{"Wine","Robertson Winery","250"},{"Wine","Queeny","250"},
        {"Champagne","Veuve Du Vernay","300"},{"Champagne","Chamdor","250"},
        {"Liquor","Baileys","30","GHS 30 / 550"},{"Liquor","Jager Meister","35","GHS 35 / 800"},{"Liquor","Campari","25","GHS 25 / 500"},
        {"Vodka","Grey Goose","35","GHS 35 / 1200"},{"Vodka","Absolute 70cl","30","GHS 30 / 500"},
        {"Vodka","Smirnoff Vodka","30","GHS 30 / 500"},{"Vodka","Smirnoff Chocolate","25","GHS 25 / 200"},
        {"Vodka","Savoy Vodka","25","GHS 25 / 250"},{"Vodka","Smirnoff 20cl","110"},
        {"Tequila","Olmeca Gold","35","GHS 35 / 500"},{"Tequila","Olmeca Silver","30","GHS 30 / 480"},
        {"Tequila","Agavita Gold","30","GHS 30 / 400"},{"Tequila","Agavita Silver","25","GHS 25 / 380"},
        {"Tequila","Messicano Silver","25","GHS 25 / 450"},
        {"Premium Whisky","Dusse","1800"},{"Premium Whisky","Double Black","1400"},{"Premium Whisky","Gold Label","1400"},
        {"Premium Whisky","Jack Daniels","35","GHS 35 / 800"},{"Premium Whisky","Black Label","35","GHS 35 / 950"},
        {"Premium Whisky","Red Label","30","GHS 30 / 600"},{"Premium Whisky","Jameson","30","GHS 30 / 650"},
        {"Premium Whisky","Ballantine","35","GHS 35 / 650"},
        {"Cognac","Hennessy V.S.O.P","80","GHS 80 / 200"},{"Cognac","Hennessy V.S","65","GHS 65 / 130"},
        {"Cognac","Couvosier","65","GHS 65 / 130"},{"Cognac","Remy Martin","75","GHS 75 / 150"},
        {"Rum","Malibu","30","GHS 30 / 500"},{"Rum","Baccadi Black","30","GHS 30 / 500"},{"Rum","Baccadi Gold","30","GHS 30 / 600"},
        {"Rum","St James White","30","GHS 30 / 500"},{"Rum","Captain Morgan","30","GHS 30 / 500"},{"Rum","Bumbu Original","850"}
    };

    String[] photos = {
        "https://raw.githubusercontent.com/paulwytte/Daily-Kitchen-Pub/main/images/banku-grilled-tilapia.jpg",
        "https://raw.githubusercontent.com/paulwytte/Daily-Kitchen-Pub/main/images/banku-salmon-pepper.jpg",
        "https://raw.githubusercontent.com/paulwytte/Daily-Kitchen-Pub/main/images/food-menu.png",
        "https://raw.githubusercontent.com/paulwytte/Daily-Kitchen-Pub/main/images/drinks-menu.png"
    };

    @Override public void onCreate(Bundle b){ super.onCreate(b); ordersPrefs=getSharedPreferences("orders",MODE_PRIVATE); buildShell(); showHome(); }

    TextView tv(String s,int sp){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(Color.WHITE);
        t.setTypeface(null,Typeface.BOLD); t.setPadding(16,12,16,12); return t;
    }
    Button btn(String s){
        Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(14);
        b.setAllCaps(false); b.setTypeface(null,Typeface.BOLD); b.setBackgroundColor(Color.rgb(45,45,45)); return b;
    }
    GradientDrawable bg(int color,float radius){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(radius); return g; }

    void buildShell(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BLACK);
        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL);
        ImageView logo=new ImageView(this); logo.setImageResource(com.dailykitchenpub.app.R.drawable.daily_kitchen_logo);
        logo.setScaleType(ImageView.ScaleType.CENTER_CROP); bar.addView(logo,new LinearLayout.LayoutParams(70,70));
        TextView title=tv("Daily Kitchen & Pub",20); title.setTextColor(GOLD);
        bar.addView(title,new LinearLayout.LayoutParams(0,82,1));
        cartBadge=tv("🛒 0",15); cartBadge.setGravity(Gravity.CENTER); bar.addView(cartBadge,new LinearLayout.LayoutParams(70,70));
        root.addView(bar);
        LinearLayout nav=new LinearLayout(this); nav.setGravity(Gravity.CENTER); nav.setPadding(4,0,4,4);
        String[] ns={"HOME","FOOD","DRINKS","PHOTOS","CART","ORDERS"};
        for(String n:ns){Button x=btn(n); nav.addView(x,new LinearLayout.LayoutParams(0,48,1));
            if(n.equals("HOME"))x.setOnClickListener(v->showHome());
            if(n.equals("FOOD"))x.setOnClickListener(v->showMenu(FOOD));
            if(n.equals("DRINKS"))x.setOnClickListener(v->showMenu(DRINKS));
            if(n.equals("PHOTOS"))x.setOnClickListener(v->showPhotos());
            if(n.equals("CART"))x.setOnClickListener(v->showCart());
            if(n.equals("ORDERS"))x.setOnClickListener(v->showOrders());
        }
        root.addView(nav);
        ScrollView scroll=new ScrollView(this); content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(12,12,12,40);
        scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
    }

    void clear(){content.removeAllViews();}
    void heading(String s){TextView h=tv(s,24); h.setTextColor(GOLD); content.addView(h);}
    void showHome(){
        clear();
        ImageView l=new ImageView(this); l.setImageResource(R.drawable.daily_kitchen_logo); l.setAdjustViewBounds(true); content.addView(l,new LinearLayout.LayoutParams(-1,300));
        heading("Where Food and Hearts Connect ❤️");
        content.addView(tv("Good Food • Cold Drinks • Great Vibes
Eat • Drink • Relax",18));
        Button order=btn("🛒 Start an Order"); order.setOnClickListener(v->showMenu(FOOD)); content.addView(order);
        Button call=btn("☎ Call 0241936496"); call.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+PHONE)))); content.addView(call);
        Button web=btn("🌐 Open Official Website"); web.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(WEBSITE)))); content.addView(web);
        content.addView(tv("📞 0241936496  •  0271938376
🔥 Hotline: 0598192068
📍 Tema Community 18 & PramPram",17));
        heading("Catering & Events");
        content.addView(tv("Book us for parties, weddings, outdoor catering, corporate events and more.",17));
        Button admin=btn("🔐 Staff / Admin Orders"); admin.setOnClickListener(v->adminLogin()); content.addView(admin);
        Button book=btn("🎉 Book Catering on WhatsApp"); book.setOnClickListener(v->openWhatsApp("Hello Daily Kitchen & Pub, I would like to make a catering/event booking.")); content.addView(book);
        heading("Special"); content.addView(tv("Saturday Special: Atieke, Fried Plantains & Tilapia — GHS 120",18));
    }

    void showMenu(String[][] data){
        clear();
        Button homeBack=btn("← HOME"); homeBack.setTextColor(Color.BLACK); homeBack.setTextSize(15); homeBack.setBackgroundColor(Color.WHITE);
        homeBack.setOnClickListener(v->showHome()); content.addView(homeBack,new LinearLayout.LayoutParams(-1,58));
        heading(data==FOOD?"Food Menu":"Drinks Menu");
        String last="";
        for(String[] row:data){
            if(!row[0].equals(last)){TextView h=tv(row[0],20);h.setTextColor(GOLD);content.addView(h);last=row[0];}
            Item item = row.length > 3 ? new Item(row[1],row[0],Integer.parseInt(row[2]),row[3]) : new Item(row[1],row[0],Integer.parseInt(row[2]));
            LinearLayout card=new LinearLayout(this); card.setGravity(Gravity.CENTER_VERTICAL); card.setPadding(8,4,8,4); card.setBackground(bg(DARK,18));
            LinearLayout info=new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL); info.setGravity(Gravity.CENTER_VERTICAL);
            TextView name=tv(row[1],17); name.setTypeface(null,Typeface.BOLD); info.addView(name);
            TextView price=tv(item.priceLabel,18); price.setTextColor(GOLD); price.setTypeface(null,Typeface.BOLD); info.addView(price);
            card.addView(info,new LinearLayout.LayoutParams(0,82,1));
            Button add=btn("Add"); add.setOnClickListener(v->{add(item);}); card.addView(add,new LinearLayout.LayoutParams(88,60));
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,100);cp.setMargins(0,4,0,6);content.addView(card,cp);
        }
    }

    void add(Item i){cart.put(i,cart.getOrDefault(i,0)+1);updateBadge();Toast.makeText(this,i.name+" added",Toast.LENGTH_SHORT).show();}
    void updateBadge(){int n=0;for(int q:cart.values())n+=q;if(cartBadge!=null)cartBadge.setText("🛒 "+n);}
    int total(){int t=0;for(Map.Entry<Item,Integer>e:cart.entrySet())t+=e.getKey().price*e.getValue();return t;}

    void showCart(){
        clear(); heading("Your Order");
        if(cart.isEmpty()){content.addView(tv("Your cart is empty. Add food or drinks first.",18));return;}
        cartList=new LinearLayout(this);cartList.setOrientation(LinearLayout.VERTICAL);content.addView(cartList);
        for(Map.Entry<Item,Integer> e:new ArrayList<>(cart.entrySet())){
            Item i=e.getKey();int q=e.getValue(); LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
            TextView t=tv(i.name+" × "+q+"\nGHS "+(i.price*q),17);row.addView(t,new LinearLayout.LayoutParams(0,65,1));
            Button minus=btn("−");minus.setOnClickListener(v->{if(cart.get(i)>1)cart.put(i,cart.get(i)-1);else cart.remove(i);showCart();updateBadge();});row.addView(minus,new LinearLayout.LayoutParams(55,55));
            Button plus=btn("+");plus.setOnClickListener(v->{add(i);showCart();});row.addView(plus,new LinearLayout.LayoutParams(55,55));cartList.addView(row);
        }
        totalView=tv("TOTAL: GHS "+total(),20);totalView.setTextColor(GOLD);content.addView(totalView);
        Button clearBtn=btn("Clear Cart");clearBtn.setOnClickListener(v->{cart.clear();updateBadge();showCart();});content.addView(clearBtn);
        heading("Customer Details");
        customerName=field("Full name");customerPhone=field("Phone number"); content.addView(customerName);content.addView(customerPhone);
        LinearLayout choices=new LinearLayout(this);pickup=new RadioButton(this);pickup.setText("Pickup");pickup.setTextColor(Color.WHITE);pickup.setTypeface(null,Typeface.BOLD);pickup.setChecked(true);
        delivery=new RadioButton(this);delivery.setText("Delivery");delivery.setTextColor(Color.WHITE);delivery.setTypeface(null,Typeface.BOLD);choices.addView(pickup);choices.addView(delivery);content.addView(choices);
        deliveryAddress=field("Delivery address (required for delivery)");content.addView(deliveryAddress); orderNotes=field("Order notes / special instructions");content.addView(orderNotes);
        Button wa=btn("📲 Send Order to WhatsApp");wa.setOnClickListener(v->checkout());content.addView(wa);
    }

    EditText field(String hint){EditText e=new EditText(this);e.setHint(hint);e.setHintTextColor(Color.LTGRAY);e.setTextColor(Color.WHITE);e.setTextSize(16);e.setTypeface(null,Typeface.BOLD);e.setSingleLine(false);e.setPadding(14,8,14,8);e.setBackground(bg(Color.rgb(35,35,35),14));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,58);p.setMargins(0,6,0,6);e.setLayoutParams(p);return e;}

    void checkout(){
        String name=customerName.getText().toString().trim(),phone=customerPhone.getText().toString().trim();
        if(name.isEmpty()||phone.isEmpty()){Toast.makeText(this,"Please enter your name and phone number.",Toast.LENGTH_LONG).show();return;}
        boolean isDelivery=delivery.isChecked();String addr=deliveryAddress.getText().toString().trim();
        if(isDelivery&&addr.isEmpty()){Toast.makeText(this,"Please enter the delivery address.",Toast.LENGTH_LONG).show();return;}
        StringBuilder s=new StringBuilder("Hello Daily Kitchen & Pub!%0A%0A*NEW ORDER*%0A");
        s.append("Customer: ").append(enc(name)).append("%0APhone: ").append(enc(phone)); s.append("%0AMethod: ").append(enc(isDelivery?"Delivery":"Pickup"));
        if(isDelivery)s.append("%0AAddress: ").append(enc(addr)); String notes=orderNotes.getText().toString().trim();if(!notes.isEmpty())s.append("%0ANotes: ").append(enc(notes));
        s.append("%0A%0A*Items*%0A"); for(Map.Entry<Item,Integer>e:cart.entrySet())s.append(enc(e.getKey().name)).append(" x").append(e.getValue()).append(" = GHS ").append(e.getKey().price*e.getValue()).append("%0A");
        int orderTotal=total();
        String orderId="DKP-"+System.currentTimeMillis();
        String summary=orderId+" | "+name+" | "+phone+" | "+(isDelivery?"Delivery":"Pickup")+" | GHS "+orderTotal+" | Pending";
        ordersPrefs.edit().putString(orderId,summary).apply();
        s.append("%0A*ORDER ID: ").append(enc(orderId)).append("*%0A*TOTAL: GHS ").append(orderTotal).append("*");
        openWhatsApp(s.toString());
        showOrderConfirmation(orderId,name,orderTotal);
    }
    void showOrderConfirmation(String id,String name,int amount){
        clear(); heading("Order Submitted");
        content.addView(tv("Thank you, "+name+"!",20));
        content.addView(tv("Order ID: "+id+"\\nTotal: GHS "+amount+"\\nStatus: PENDING",18));
        content.addView(tv("Your order has been sent to Daily Kitchen & Pub on WhatsApp. The restaurant can confirm it and update the order status.",17));
        Button home=btn("← HOME"); home.setTextColor(Color.BLACK); home.setBackgroundColor(Color.WHITE); home.setOnClickListener(v->showHome()); content.addView(home);
        Button orders=btn("View My Orders"); orders.setOnClickListener(v->showOrders()); content.addView(orders);
    }

    void adminLogin(){
        final EditText pin=field("Staff PIN");
        new AlertDialog.Builder(this).setTitle("Staff / Admin Login").setMessage("Enter the restaurant staff PIN to manage orders.")
            .setView(pin).setPositiveButton("LOGIN",(d,w)->{
                if("2026".equals(pin.getText().toString().trim())) showAdminOrders();
                else Toast.makeText(this,"Incorrect PIN.",Toast.LENGTH_LONG).show();
            }).setNegativeButton("Cancel",null).show();
    }

    void showAdminOrders(){
        clear(); heading("Staff Order Dashboard");
        content.addView(tv("Manage customer orders, totals, payment and preparation status.",17));
        if(ordersPrefs.getAll().isEmpty()){content.addView(tv("No orders have been received on this device.",18));return;}
        for(Map.Entry<String,?> e:ordersPrefs.getAll().entrySet()){
            String id=e.getKey(); String summary=String.valueOf(e.getValue());
            LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(10,8,10,8); card.setBackground(bg(DARK,18));
            TextView t=tv(summary.replace(" | ","\\n"),16); card.addView(t);
            LinearLayout actions=new LinearLayout(this);
            Button confirm=btn("Confirm Order"); confirm.setOnClickListener(v->{updateOrderStatus(id,"Confirmed");showAdminOrders();});
            Button paid=btn("Payment Confirmed"); paid.setOnClickListener(v->{updateOrderStatus(id,"Payment Confirmed");showAdminOrders();});
            Button prep=btn("Preparing"); prep.setOnClickListener(v->{updateOrderStatus(id,"Preparing");showAdminOrders();});
            Button ready=btn("Ready/Delivered"); ready.setOnClickListener(v->{updateOrderStatus(id,"Ready/Delivered");showAdminOrders();});
            Button cancel=btn("Cancel"); cancel.setOnClickListener(v->{updateOrderStatus(id,"Cancelled");showAdminOrders();});
            actions.addView(confirm,new LinearLayout.LayoutParams(0,55,1)); actions.addView(paid,new LinearLayout.LayoutParams(0,55,1));
            actions.addView(prep,new LinearLayout.LayoutParams(0,55,1)); actions.addView(ready,new LinearLayout.LayoutParams(0,55,1)); actions.addView(cancel,new LinearLayout.LayoutParams(0,55,1));
            card.addView(actions);
            Button wa=btn("WhatsApp Customer"); wa.setOnClickListener(v->{
                String phone=summary.split(" \\| ")[2].trim();
                openWhatsAppTo(phone,"Daily Kitchen & Pub order "+id+" has been updated. Please contact us if you need assistance.");
            }); card.addView(wa);
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,6,0,6);content.addView(card,p);
        }
    }

    void openWhatsAppTo(String phone,String message){
        String digits=phone.replaceAll("[^0-9]","");
        if(digits.startsWith("0")) digits="233"+digits.substring(1);
        Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://wa.me/"+digits+"?text="+enc(message)));
        try{startActivity(i);}catch(Exception e){Toast.makeText(this,"WhatsApp is not installed.",Toast.LENGTH_LONG).show();}
    }

    void showOrders(){
        clear(); heading("Orders");
        content.addView(tv("Orders saved on this device",17));
        if(ordersPrefs.getAll().isEmpty()){content.addView(tv("No orders yet.",18));return;}
        for(Map.Entry<String,?> e:ordersPrefs.getAll().entrySet()){
            String id=e.getKey(); String summary=String.valueOf(e.getValue());
            LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(10,8,10,8); card.setBackground(bg(DARK,18));
            TextView t=tv(summary.replace(" | ","\\n"),16); card.addView(t);
            LinearLayout actions=new LinearLayout(this);
            Button confirm=btn("Confirm"); confirm.setOnClickListener(v->{updateOrderStatus(id,"Confirmed");showOrders();});
            Button preparing=btn("Preparing"); preparing.setOnClickListener(v->{updateOrderStatus(id,"Preparing");showOrders();});
            Button ready=btn("Ready/Delivered"); ready.setOnClickListener(v->{updateOrderStatus(id,"Ready/Delivered");showOrders();});
            Button cancel=btn("Cancel"); cancel.setOnClickListener(v->{updateOrderStatus(id,"Cancelled");showOrders();});
            actions.addView(confirm,new LinearLayout.LayoutParams(0,55,1)); actions.addView(preparing,new LinearLayout.LayoutParams(0,55,1)); actions.addView(ready,new LinearLayout.LayoutParams(0,55,1)); actions.addView(cancel,new LinearLayout.LayoutParams(0,55,1));
            card.addView(actions); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,6,0,6);content.addView(card,p);
        }
    }

    void updateOrderStatus(String id,String status){
        String old=ordersPrefs.getString(id,"");
        int p=old.lastIndexOf(" | ");
        if(p>=0) old=old.substring(0,p)+" | "+status;
        ordersPrefs.edit().putString(id,old).apply();
    }

    String enc(String s){try{return URLEncoder.encode(s,StandardCharsets.UTF_8.toString()).replace("+","%20");}catch(Exception e){return s.replace(" ","%20");}}
    void openWhatsApp(String message){Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://wa.me/"+WHATSAPP+"?text="+message));try{startActivity(i);}catch(Exception e){Toast.makeText(this,"WhatsApp is not installed.",Toast.LENGTH_LONG).show();}}

    void showPhotos(){
        clear();heading("Food Photos"); content.addView(tv("Our food and menu gallery",17));
        for(String url:photos){ImageView iv=new ImageView(this);iv.setAdjustViewBounds(true);iv.setScaleType(ImageView.ScaleType.CENTER_CROP);content.addView(iv,new LinearLayout.LayoutParams(-1,240));loadImage(url,iv);}
    }
    void loadImage(String url,ImageView target){executor.submit(()->{try(InputStream in=new URL(url).openStream()){Bitmap b=BitmapFactory.decodeStream(in);runOnUiThread(()->{if(b!=null)target.setImageBitmap(b);});}catch(Exception ignored){}});}
    @Override protected void onDestroy(){executor.shutdownNow();super.onDestroy();}
}