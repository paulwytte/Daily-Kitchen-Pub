package com.dailykitchenpub.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val website = "https://paulwytte.github.io/Daily-Kitchen-Pub/"
    private val whatsapp = "https://wa.me/233241936496"
    private val phone = "tel:0241936496"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.foodMenu).text = foodMenu()
        findViewById<TextView>(R.id.drinksMenu).text = drinksMenu()

        findViewById<MaterialButton>(R.id.orderButton).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(whatsapp)))
        }
        findViewById<MaterialButton>(R.id.callButton).setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(phone)))
        }
        findViewById<MaterialButton>(R.id.websiteButton).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(website)))
        }
    }

    private fun foodMenu() = """
LOCAL DISHES
Banku & Okro with Tilapia — GHS 70
Banku with Tilapia Pepper — GHS 80
Banku with Grilled Tilapia — GHS 80
Banku with Salmon Pepper — GHS 80
Akple with Ademey — GHS 80
Fufu with Goat Soup — GHS 160
Omotuo with Groundnut Soup — GHS 100
Aborbitadi — GHS 70

ADDITIONAL LOCAL
Banku with Ademey — GHS 70
Ewokple with Aborbitadi — GHS 160
Ewokple with Salmon Pepper — GHS 80

RICE DISHES
Jollof Rice with Goat Meat — GHS 120
Jollof Rice with Cow Meat — GHS 130
Assorted Jollof Rice Special — GHS 150
Assorted Fried Rice Special — GHS 150
Plain Rice with Egg Stew — GHS 130
Plain Rice with Egg Stew & Goat Meat — GHS 160
Plain Rice with Egg Stew & Cow Meat — GHS 160

YAM & PASTA
Boiled Yam with Egg Stew — GHS 100
Fried Yam with Pork (Optional) — GHS 130
Assorted Spaghetti — GHS 130

GRILLS & SIDES
Turkey — GHS 130
Pork — GHS 130
Chicken Wings — GHS 90
Salad — GHS 40

SOUPS
Goat Soup — GHS 100
Chicken Soup — GHS 80
Groundnut Soup — GHS 100
Palm Nut Soup — GHS 80

SATURDAY SPECIAL
Atieke, Fried Plantains & Tilapia — GHS 120
""".trimIndent()

    private fun drinksMenu() = """
NATURAL FRUIT DRINKS
Fresh Juice — GHS 30
Hibiscus Drink (Zobo) — GHS 25
Tropical Juice — GHS 30
Pineapple Juice — GHS 30

SOFT DRINKS
Malt — GHS 15
B.B Cocktail — GHS 15
Alvaro — GHS 15
Sprite — GHS 15
Fanta — GHS 15
Uni Fresh — GHS 20
Ceres — GHS 80
Cranberry — GHS 80
Don Simond Cocktail — GHS 70
Fru Telli — GHS 70
1 litre Coke — GHS 50
Plastic Medium Coke — GHS 15
Water 500ml — GHS 5
Water 750ml — GHS 10

BEER
Guinness — GHS 15
Mini Club — GHS 15
Club Shandy — GHS 15
Heineken Bottle — GHS 30
Stella — GHS 30
Budweiser — GHS 30
Heineken Can — GHS 25
Orijin — GHS 25
Guilder — GHS 20
Large Club — GHS 20
Star — GHS 25
Eagle — GHS 20

CIDERS & ENERGY
Smirnoff Ice — GHS 25 / 500
Hunters Gold — GHS 25 / 500
Savanna Dry — GHS 25 / 500
Kiss — GHS 30
Rox — GHS 30
Red Bull — GHS 30
Vody — GHS 25
Black Bullet — GHS 25

WHISKY
Johnnie Walker Red Label — GHS 120
Black Label — GHS 180
Jack Daniel’s — GHS 150
Jameson — GHS 150
Chivas Regal — GHS 200
Ballantine’s — GHS 120
""".trimIndent()
}
