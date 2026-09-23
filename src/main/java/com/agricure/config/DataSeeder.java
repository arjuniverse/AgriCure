package com.agricure.config;

import com.agricure.entity.Disease;
import com.agricure.entity.Plant;
import com.agricure.entity.PlantDisease;
import com.agricure.entity.Role;
import com.agricure.entity.User;
import com.agricure.repository.DiseaseRepository;
import com.agricure.repository.PlantDiseaseRepository;
import com.agricure.repository.PlantRepository;
import com.agricure.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final DiseaseRepository diseaseRepository;
    private final PlantDiseaseRepository plantDiseaseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PlantRepository plantRepository, DiseaseRepository diseaseRepository,
                      PlantDiseaseRepository plantDiseaseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
        this.diseaseRepository = diseaseRepository;
        this.plantDiseaseRepository = plantDiseaseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedUsers();
        if (plantRepository.count() == 0) {
            seedCatalog();
        }
    }

    private void seedUsers() {
        if (!userRepository.existsByEmailIgnoreCase("admin@agricure.com")) {
            User admin = new User();
            admin.setFullName("AgriCure Admin");
            admin.setEmail("admin@agricure.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
        if (!userRepository.existsByEmailIgnoreCase("farmer@agricure.com")) {
            User farmer = new User();
            farmer.setFullName("Demo Farmer");
            farmer.setEmail("farmer@agricure.com");
            farmer.setPassword(passwordEncoder.encode("Farmer@123"));
            farmer.setRole(Role.USER);
            userRepository.save(farmer);
        }
    }

    private void seedCatalog() {
        Plant tomato = plant("Tomato", "Solanum lycopersicum", "Vegetable",
                "Warm-season fruiting crop grown worldwide for fresh market and processing.", "Temperate and tropical");
        Plant potato = plant("Potato", "Solanum tuberosum", "Tuber",
                "Staple tuber crop sensitive to late blight and several soil-borne pathogens.", "Cool temperate");
        Plant wheat = plant("Wheat", "Triticum aestivum", "Cereal",
                "Major cereal grain used for flour. Yield is strongly affected by rusts and mildews.", "Temperate");
        Plant rice = plant("Rice", "Oryza sativa", "Cereal",
                "Flooded or upland cereal that feeds billions. Bacterial blight and blast are key threats.", "Tropical and subtropical");
        Plant maize = plant("Maize", "Zea mays", "Cereal",
                "High-yielding cereal also called corn. Leaf blights and rusts commonly reduce grain fill.", "Tropical to temperate");
        Plant apple = plant("Apple", "Malus domestica", "Fruit",
                "Perennial orchard crop. Scab and fire blight are among the most damaging diseases.", "Temperate");
        Plant grape = plant("Grape", "Vitis vinifera", "Fruit",
                "Vine crop used for table fruit and wine. Downy and powdery mildew are frequent in humid climates.", "Mediterranean and temperate");
        Plant citrus = plant("Citrus", "Citrus spp.", "Fruit",
                "Evergreen fruit trees including orange, lemon, and lime. Greening and canker are serious threats.", "Subtropical");

        Disease earlyBlight = disease("Early blight", "MEDIUM",
                "Concentric brown leaf spots, yellowing from the base, stem lesions, and fruit rot with dark sunken rings.",
                "Alternaria fungi favored by warm weather, overhead irrigation, and residual crop debris.",
                "Rotate crops, remove infected leaves, mulch soil, and avoid wetting foliage late in the day.",
                "Apply labeled copper or chlorothalonil products at first spots. Improve airflow and reduce leaf wetness.",
                "leaf spots, concentric rings, yellowing, blight, fruit rot, target spots");
        Disease lateBlight = disease("Late blight", "HIGH",
                "Water-soaked lesions that rapidly blacken leaves, white fungal growth under leaves in humid weather, and tuber or fruit rot.",
                "Phytophthora infestans spreads in cool, wet conditions via wind-blown spores.",
                "Plant certified seed, destroy volunteer plants, and avoid overhead irrigation during cool nights.",
                "Remove infected plants immediately. Use protectant fungicides preventively in outbreak weather.",
                "water-soaked, blackened leaves, white mold, rapid blight, tuber rot, greasy spots");
        Disease powderyMildew = disease("Powdery mildew", "MEDIUM",
                "White powdery coating on upper leaves, distorted young growth, and reduced fruit quality.",
                "Fungal pathogens favored by moderate temperatures, high humidity, and dense canopies.",
                "Prune for airflow, avoid excess nitrogen, and choose resistant cultivars where available.",
                "Sulfur or potassium bicarbonate sprays and canopy thinning usually slow the epidemic.",
                "white powder, dusty leaves, mildew, distorted shoots, leaf curling");
        Disease downyMildew = disease("Downy mildew", "HIGH",
                "Yellow angular leaf spots with gray-purple downy growth on the underside of leaves, sometimes rapid defoliation.",
                "Oomycete pathogens that thrive in cool, wet nights and splash dispersal.",
                "Improve drainage, space plants, and keep foliage dry. Remove heavily infected leaves.",
                "Apply phosphonate or copper products labeled for downy mildew at the first yellow spots.",
                "yellow spots, angular lesions, gray mold underside, downy, oily patches");
        Disease bacterialBlight = disease("Bacterial blight", "HIGH",
                "Water-soaked leaf streaks or spots that turn brown, leaf wilting, and milky bacterial ooze in severe rice cases.",
                "Xanthomonas bacteria entering through wounds or hydathodes, spread by rain splash and tools.",
                "Use clean seed, resistant varieties, and avoid working fields when plants are wet.",
                "Copper bactericides have limited effect. Rogue infected plants and sanitize tools.",
                "water-soaked streaks, brown lesions, wilt, ooze, leaf blight, yellow margins");
        Disease rust = disease("Leaf rust", "MEDIUM",
                "Orange, brown, or yellow pustules on leaves that rub off as powder, causing premature drying.",
                "Puccinia fungi requiring living hosts and wind-dispersed urediniospores.",
                "Grow resistant cultivars and avoid late nitrogen that keeps canopies lush and susceptible.",
                "Triazole or strobilurin fungicides timed to first pustules protect remaining green leaf area.",
                "orange pustules, rust powder, brown spots, yellowing, leaf drying");
        Disease blast = disease("Rice blast", "HIGH",
                "Diamond-shaped gray leaf spots with brown borders, rotten necks that snap, and unfilled grain.",
                "Magnaporthe oryzae favored by high humidity, dense planting, and excess nitrogen.",
                "Balanced fertilizer, wider spacing, and resistant varieties reduce outbreaks.",
                "Apply labeled blast fungicides at tillering or heading if lesions appear on upper leaves.",
                "diamond spots, gray centers, neck rot, broken stems, empty grain, leaf blast");
        Disease scab = disease("Apple scab", "MEDIUM",
                "Olive-green to black velvety spots on leaves and fruit, cracked fruit skin, and early leaf drop.",
                "Venturia inaequalis overwinters in fallen leaves and infects during prolonged spring wetness.",
                "Rake and destroy fallen leaves, prune for airflow, and plant scab-resistant cultivars.",
                "Protectant fungicides from green tip through fruit set are the standard orchard program.",
                "olive spots, black lesions, scabby fruit, cracked skin, leaf drop");
        Disease citrusCanker = disease("Citrus canker", "HIGH",
                "Raised corky lesions with water-soaked margins on leaves, stems, and fruit, often surrounded by a yellow halo.",
                "Xanthomonas citri spread by wind-driven rain, contaminated tools, and infected nursery stock.",
                "Use certified plants, windbreaks, and strict sanitation. Do not move infected material.",
                "Copper sprays protect new flushes. Severely infected trees may need removal in quarantine areas.",
                "corky lesions, yellow halo, raised spots, fruit scabs, leaf canker");
        Disease mosaic = disease("Mosaic virus", "MEDIUM",
                "Mottled light and dark green leaf patterns, stunting, leaf curling, and reduced fruit set.",
                "Viruses transmitted by aphids, whiteflies, or contaminated tools depending on the crop.",
                "Control insect vectors, remove weeds, and disinfect tools. Plant virus-indexed seedlings.",
                "There is no chemical cure. Rogue infected plants and manage insects to stop further spread.",
                "mosaic, mottled leaves, stunting, leaf curl, yellow patches, distorted fruit");

        map(tomato, earlyBlight, "Very common in warm humid tomato fields");
        map(tomato, lateBlight, "Destructive during cool wet spells");
        map(tomato, powderyMildew, "Appears in greenhouses and dense plantings");
        map(tomato, mosaic, "Often introduced by aphids or infected seedlings");
        map(potato, lateBlight, "Historic cause of severe tuber losses");
        map(potato, earlyBlight, "Builds up on older foliage mid-season");
        map(wheat, rust, "Classic yield robber on susceptible varieties");
        map(wheat, powderyMildew, "Favored by dense lush canopies");
        map(rice, bacterialBlight, "Major wet-season disease");
        map(rice, blast, "Can destroy panicles within days");
        map(maize, rust, "Common on late-planted maize");
        map(maize, downyMildew, "Serious in humid tropical production");
        map(apple, scab, "Primary spring disease in wet orchards");
        map(apple, powderyMildew, "Attacks young shoots and fruit russet");
        map(grape, downyMildew, "Explosive in wet springs");
        map(grape, powderyMildew, "Can scar berries even in drier weather");
        map(citrus, citrusCanker, "Quarantine-significant bacterial disease");
        map(citrus, mosaic, "Virus-like mottling on some citrus types");
    }

    private Plant plant(String name, String scientific, String category, String description, String region) {
        Plant plant = new Plant();
        plant.setName(name);
        plant.setScientificName(scientific);
        plant.setCategory(category);
        plant.setDescription(description);
        plant.setGrowingRegion(region);
        return plantRepository.save(plant);
    }

    private Disease disease(String name, String severity, String symptoms, String causes, String prevention,
                            String treatment, String keywords) {
        Disease disease = new Disease();
        disease.setName(name);
        disease.setSeverity(severity);
        disease.setSymptoms(symptoms);
        disease.setCauses(causes);
        disease.setPrevention(prevention);
        disease.setTreatment(treatment);
        disease.setSymptomKeywords(keywords);
        return diseaseRepository.save(disease);
    }

    private void map(Plant plant, Disease disease, String notes) {
        PlantDisease mapping = new PlantDisease();
        mapping.setPlant(plant);
        mapping.setDisease(disease);
        mapping.setNotes(notes);
        plantDiseaseRepository.save(mapping);
    }
}
