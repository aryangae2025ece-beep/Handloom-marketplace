package com.handloom.marketplace.config;

import com.handloom.marketplace.entity.Category;
import com.handloom.marketplace.entity.Product;
import com.handloom.marketplace.entity.Role;
import com.handloom.marketplace.entity.User;
import com.handloom.marketplace.repository.CategoryRepository;
import com.handloom.marketplace.repository.ProductRepository;
import com.handloom.marketplace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Creates the admin account and demo data on first start (only when the database is empty). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, CategoryRepository categories,
                      ProductRepository products, PasswordEncoder encoder) {
        this.users = users;
        this.categories = categories;
        this.products = products;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        User admin = users.findByEmail("admin@handloom.com").orElse(null);
        if (admin == null) {
            makeUser("Site Admin", "admin@handloom.com", "Arya@admin123", Role.ADMIN, null, null, null);
        } else {
            admin.setPassword(encoder.encode("Arya@admin123"));
            users.save(admin);
        }
        if (categories.count() > 0) {
            return;
        }

        Map<String, Category> cat = new LinkedHashMap<>();
        cat.put("Sarees", makeCategory("Sarees", "bi-flower1"));
        cat.put("Shawls & Stoles", makeCategory("Shawls & Stoles", "bi-wind"));
        cat.put("Home Decor", makeCategory("Home Decor", "bi-house-heart"));
        cat.put("Pottery", makeCategory("Pottery", "bi-cup-hot"));
        cat.put("Jewellery", makeCategory("Jewellery", "bi-gem"));
        cat.put("Bags & Accessories", makeCategory("Bags & Accessories", "bi-handbag"));

        if (!users.existsByEmail("customer@handloom.com")) {
            makeUser("Priya Nair", "customer@handloom.com", "customer123", Role.CUSTOMER,
                    "12, Gandhi Road, Tiruchengode, Tamil Nadu 637211", "9876543210", null);
        }

        User lakshmi = makeUser("Lakshmi Devi", "lakshmi@handloom.com", "artisan123", Role.ARTISAN,
                "Kanchipuram, Tamil Nadu", "9840012345",
                "I am a third-generation silk weaver from Kanchipuram. My family has woven temple-border "
                        + "sarees on the same wooden loom for sixty years. Each saree takes 10 to 15 days, "
                        + "with real zari and pure mulberry silk.");
        lakshmi.setRegion("Kanchipuram, Tamil Nadu");
        User farhan = makeUser("Farhan Ansari", "farhan@handloom.com", "artisan123", Role.ARTISAN,
                "Varanasi, Uttar Pradesh", "9935012345",
                "My workshop in Varanasi weaves Banarasi brocade the traditional way, with gold-"
                        + "and silver-thread motifs inspired by the ghats and Mughal gardens.");
        farhan.setRegion("Varanasi, Uttar Pradesh");
        User rashid = makeUser("Abdul Rashid", "rashid@handloom.com", "artisan123", Role.ARTISAN,
                "Srinagar, Jammu & Kashmir", "9906012345",
                "I hand-embroider and weave pashmina in Srinagar. Every shawl is made from fibre "
                        + "combed by hand in Ladakh and finished with needlework passed down in my family.");
        rashid.setRegion("Srinagar, Jammu & Kashmir");
        User meera = makeUser("Meera Shah", "meera@handloom.com", "artisan123", Role.ARTISAN,
                "Bhuj, Kutch, Gujarat", "9825012345",
                "I lead a group of 30 women embroiderers in Kutch. Our mirror-work and block-print "
                        + "pieces keep alive stitches that our grandmothers taught us.");
        meera.setRegion("Bhuj, Kutch, Gujarat");
        User suresh = makeUser("Suresh Kumar", "suresh@handloom.com", "artisan123", Role.ARTISAN,
                "Jaipur, Rajasthan", "9414012345",
                "I make blue pottery and lac bangles in Jaipur. Blue pottery uses quartz dough, "
                        + "not clay, and is fired only once at a low temperature.");
        suresh.setRegion("Jaipur, Rajasthan");
        User ramesh = makeUser("Ramesh Goud", "ramesh@handloom.com", "artisan123", Role.ARTISAN,
                "Pochampally, Telangana", "9848012345",
                "Our Pochampally cooperative ties and dyes the threads before weaving, which is what "
                        + "gives ikat its soft, feathered edges. Twenty families work on our looms.");
        ramesh.setRegion("Pochampally, Telangana");
        users.saveAll(java.util.List.of(lakshmi, farhan, rashid, meera, suresh, ramesh));

        makeProduct(lakshmi, cat.get("Sarees"), "Kanchipuram Pure Silk Saree, Peacock Green", 18500, 5,
                "Kanchipuram Silk", "Tamil Nadu", "saree-green",
                "Pure mulberry silk with real zari temple border and a rich peacock-green body. "
                        + "Woven on a pit loom over 12 days. Comes with a silk-mark certificate.");
        makeProduct(lakshmi, cat.get("Sarees"), "Kanchipuram Temple Border Saree, Maroon and Gold", 22000, 3,
                "Kanchipuram Silk", "Tamil Nadu", "saree-maroon",
                "A bridal-weight maroon saree with a broad gold temple border and rudraksh buttas across the body.");
        makeProduct(lakshmi, cat.get("Sarees"), "Madurai Sungudi Cotton Saree", 2400, 14,
                "Sungudi Tie-Dye Cotton", "Tamil Nadu", "saree-sungudi",
                "Light everyday cotton with hand-tied dots in the traditional Sungudi style. Soft, breathable, easy to drape.");
        makeProduct(farhan, cat.get("Sarees"), "Banarasi Brocade Silk Saree, Royal Blue", 12900, 6,
                "Banarasi Brocade", "Uttar Pradesh", "saree-blue",
                "Royal-blue Banarasi silk with gold brocade motifs and a heavy pallu. A wedding-season favourite.");
        makeProduct(ramesh, cat.get("Sarees"), "Pochampally Ikat Cotton Saree", 3200, 10,
                "Pochampally Ikat", "Telangana", "saree-ikat",
                "Geometric ikat woven from pre-dyed threads, so the pattern is part of the fabric and never fades.");
        makeProduct(rashid, cat.get("Shawls & Stoles"), "Kashmiri Kani Pashmina Shawl", 14500, 4,
                "Pashmina", "Jammu & Kashmir", "shawl-kani",
                "Fine pashmina woven with the kani technique using small wooden bobbins. One shawl takes months.");
        makeProduct(rashid, cat.get("Shawls & Stoles"), "Sozni Embroidered Wool Stole", 4800, 8,
                "Sozni Embroidery", "Jammu & Kashmir", "shawl-sozni",
                "A soft wool stole with delicate hand sozni needlework along the border. Light and warm.");
        makeProduct(farhan, cat.get("Shawls & Stoles"), "Banarasi Silk Dupatta", 3800, 9,
                "Banarasi Weave", "Uttar Pradesh", "dupatta",
                "Silk dupatta with woven zari border and small buttas. Pairs well with plain kurtas and lehengas.");
        makeProduct(ramesh, cat.get("Home Decor"), "Ikat Cushion Cover Set (2 pieces)", 1100, 25,
                "Pochampally Ikat", "Telangana", "cushion-ikat",
                "Two 16-inch cushion covers in handwoven ikat cotton with a hidden zip. Colour-fast and machine washable.");
        makeProduct(meera, cat.get("Home Decor"), "Kutch Mirror-Work Wall Hanging", 1650, 10,
                "Kutch Embroidery", "Gujarat", "wallhang",
                "Hand-stitched shisha mirrors on a bright cotton base, finished with a wooden hanging rod.");
        makeProduct(meera, cat.get("Home Decor"), "Handloom Cotton Bedsheet, Indigo Block Print", 2200, 12,
                "Ajrakh Block Print", "Gujarat", "bedsheet",
                "Double bedsheet in naturally dyed indigo, block-printed by hand. Comes with two pillow covers.");
        makeProduct(suresh, cat.get("Pottery"), "Jaipur Blue Pottery Vase", 950, 15,
                "Blue Pottery", "Rajasthan", "pot-vase",
                "Hand-painted floral vase in classic cobalt blue on a white glaze. Not clay: it is made from quartz dough.");
        makeProduct(suresh, cat.get("Pottery"), "Terracotta Tea Set with 6 Cups", 1450, 7,
                "Terracotta", "Rajasthan", "pot-tea",
                "Wheel-thrown terracotta teapot with six cups. Keeps tea warm and gives it a light earthy taste.");
        makeProduct(meera, cat.get("Jewellery"), "Oxidised Silver Jhumka Earrings", 750, 20,
                "Oxidised Silver", "Gujarat", "jewel-jhumka",
                "Traditional bell-shaped jhumkas with tiny hand-engraved dots. Lightweight for all-day wear.");
        makeProduct(suresh, cat.get("Jewellery"), "Jaipur Lac Bangle Set (12 pieces)", 650, 30,
                "Lac Bangles", "Rajasthan", "jewel-bangles",
                "A set of 12 colourful lac bangles, each hand-rolled and decorated. Available in size 2.4 and 2.6.");
        makeProduct(meera, cat.get("Bags & Accessories"), "Kutch Embroidered Sling Bag", 1350, 13,
                "Kutch Embroidery", "Gujarat", "bag-kutch",
                "Cotton sling bag with dense hand embroidery, an inner pocket and an adjustable strap.");

        System.out.println("=== Kaithari demo data created ===");
        System.out.println("Admin   : admin@handloom.com    / admin123");
        System.out.println("Artisan : lakshmi@handloom.com  / artisan123");
        System.out.println("Customer: customer@handloom.com / customer123");
    }

    private Category makeCategory(String name, String icon) {
        Category c = new Category();
        c.setName(name);
        c.setIcon(icon);
        return categories.save(c);
    }

    private User makeUser(String name, String email, String rawPassword, Role role,
                          String address, String phone, String bio) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPassword));
        u.setRole(role);
        u.setAddress(address);
        u.setPhone(phone);
        u.setBio(bio);
        return users.save(u);
    }

    private void makeProduct(User artisan, Category category, String name, int price, int stock,
                             String craft, String region, String image, String description) {
        Product p = new Product();
        p.setArtisan(artisan);
        p.setCategory(category);
        p.setName(name);
        p.setPrice(BigDecimal.valueOf(price));
        p.setStock(stock);
        p.setCraftType(craft);
        p.setRegion(region);
        p.setDescription(description);
        p.setImageUrl("/images/products/" + image + ".svg");
        products.save(p);
    }
}
