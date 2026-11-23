package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.dto.RecommendationRequest;
import com.smarttourism.smart_tourism_backend.dto.RecommendedPlaceResponse;
import com.smarttourism.smart_tourism_backend.dto.SolapurGuideResponse;
import com.smarttourism.smart_tourism_backend.entity.TouristPlace;
import com.smarttourism.smart_tourism_backend.repository.TouristPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class RecommendationService {

    private final TouristPlaceRepository touristPlaceRepository;
    private final GeminiService geminiService;

    public RecommendationService(
            TouristPlaceRepository touristPlaceRepository,
            GeminiService geminiService) {
        this.touristPlaceRepository = touristPlaceRepository;
        this.geminiService = geminiService;
    }

    public List<RecommendedPlaceResponse> recommend(RecommendationRequest request) {
        validate(request);

        double minimumRating = request.getMinimumRating() == null ? 0 : request.getMinimumRating();
        String preferredCategory = normalize(request.getPreferredCategory());
        List<TouristPlace> allCandidates = touristPlaceRepository.findAll().stream()
                .filter(place -> categoryMatches(place, preferredCategory))
                .filter(place -> place.getTicketPrice() == null || place.getTicketPrice() <= request.getBudget())
                .filter(place -> minimumRating == 0
                        || (place.getRating() != null && place.getRating() >= minimumRating))
                .sorted(Comparator.comparing(
                        (TouristPlace p) -> interestScore(p, request.getInterests()),
                        Comparator.reverseOrder()
                ).thenComparing(
                        TouristPlace::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();

        if (allCandidates.isEmpty()) {
            return List.of();
        }

        List<TouristPlace> candidates = allCandidates.size() > 25
                ? allCandidates.subList(0, 25)
                : allCandidates;

        var candidatesById = candidates.stream()
                .collect(java.util.stream.Collectors.toMap(TouristPlace::getPlaceId, place -> place));

        List<GeminiService.PlaceRecommendation> selections = (request.getLanguage() != null && !request.getLanguage().isBlank())
                ? geminiService.recommendPlaces(
                        candidates,
                        request.getLocation().trim(),
                        request.getBudget(),
                        request.getDays(),
                        request.getTravelPreference().trim(),
                        request.getInterests(),
                        request.getPreferredCategory() == null ? "" : request.getPreferredCategory().trim(),
                        minimumRating,
                        request.getLanguage()
                )
                : geminiService.recommendPlaces(
                        candidates,
                        request.getLocation().trim(),
                        request.getBudget(),
                        request.getDays(),
                        request.getTravelPreference().trim(),
                        request.getInterests(),
                        request.getPreferredCategory() == null ? "" : request.getPreferredCategory().trim(),
                        minimumRating
                );

        return selections.stream()
                .map(selection -> toResponse(candidatesById.get(selection.placeId()), selection.reason(), selection.speciality()))
                .toList();
    }

    private int interestScore(TouristPlace place, List<String> interests) {
        if (interests == null || interests.isEmpty()) return 0;
        int score = 0;
        String text = (safeString(place.getPlaceName()) + " "
                + safeString(place.getCategory()) + " "
                + safeString(place.getSubCategory()) + " "
                + safeString(place.getDescription()) + " "
                + safeString(place.getLocation())).toLowerCase(Locale.ROOT);

        for (String interest : interests) {
            String norm = interest.toLowerCase(Locale.ROOT);
            if ((norm.contains("temple") || norm.contains("pilgrim") || norm.contains("spiritual"))
                    && (text.contains("temple") || text.contains("mandir") || text.contains("shrine") || text.contains("samarth") || text.contains("vitthal") || text.contains("ashram"))) {
                score += 3;
            }
            if ((norm.contains("histor") || norm.contains("fort") || norm.contains("heritage"))
                    && (text.contains("fort") || text.contains("histor") || text.contains("monument") || text.contains("heritage") || text.contains("chalukya") || text.contains("bhuikot") || text.contains("naldurg"))) {
                score += 3;
            }
            if ((norm.contains("nature") || norm.contains("wildlife") || norm.contains("scenic"))
                    && (text.contains("lake") || text.contains("bird") || text.contains("wildlife") || text.contains("sanctuary") || text.contains("nature") || text.contains("dam") || text.contains("bustard"))) {
                score += 3;
            }
            if (norm.contains("food")
                    && (text.contains("food") || text.contains("bazaar") || text.contains("market") || text.contains("agro") || text.contains("resort"))) {
                score += 2;
            }
            if (norm.contains("shop")
                    && (text.contains("shop") || text.contains("market") || text.contains("chaddar") || text.contains("textile") || text.contains("handloom") || text.contains("peth"))) {
                score += 2;
            }
            if (norm.contains("family")
                    && (text.contains("park") || text.contains("lake") || text.contains("temple") || text.contains("fort") || text.contains("resort") || text.contains("water"))) {
                score += 2;
            }
            if (norm.contains("adventur")
                    && (text.contains("fort") || text.contains("water") || text.contains("trek") || text.contains("sanctuary"))) {
                score += 2;
            }
            if (norm.contains("budget") && (place.getTicketPrice() == null || place.getTicketPrice() <= 25)) {
                score += 2;
            }
        }
        return score;
    }

    private String safeString(String str) {
        return str == null ? "" : str;
    }

    private void validate(RecommendationRequest request) {
        if (request == null
                || request.getLocation() == null
                || request.getLocation().isBlank()
                || request.getLocation().length() > 100) {
            throw new ResponseStatusException(BAD_REQUEST, "Enter a location up to 100 characters");
        }
        if (!Double.isFinite(request.getBudget()) || request.getBudget() < 0 || request.getBudget() > 10_000_000) {
            throw new ResponseStatusException(BAD_REQUEST, "Budget must be between 0 and 10,000,000");
        }
        if (request.getDays() < 1 || request.getDays() > 30) {
            throw new ResponseStatusException(BAD_REQUEST, "Trip duration must be between 1 and 30 days");
        }
        if (request.getTravelPreference() == null || request.getTravelPreference().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Select a travel preference");
        }
        if (request.getInterests() == null || request.getInterests().isEmpty()
                || request.getInterests().size() > 10
                || request.getInterests().stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new ResponseStatusException(BAD_REQUEST, "Select between 1 and 10 interests");
        }
        if (request.getPreferredCategory() != null && request.getPreferredCategory().length() > 100) {
            throw new ResponseStatusException(BAD_REQUEST, "Preferred category is too long");
        }
        Double minimumRating = request.getMinimumRating();
        if (minimumRating != null && (!Double.isFinite(minimumRating) || minimumRating < 0 || minimumRating > 5)) {
            throw new ResponseStatusException(BAD_REQUEST, "Minimum rating must be between 0 and 5");
        }
    }

    private boolean categoryMatches(TouristPlace place, String preferredCategory) {
        return preferredCategory.isEmpty()
                || preferredCategory.equals(normalize(place.getCategory()))
                || preferredCategory.equals(normalize(place.getSubCategory()));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private RecommendedPlaceResponse toResponse(TouristPlace place, String reason, String speciality) {
        String finalSpeciality = (speciality != null && !speciality.isBlank())
                ? speciality
                : deriveSpeciality(place);
        return new RecommendedPlaceResponse(
                place.getPlaceId(),
                place.getPlaceName(),
                place.getCategory(),
                place.getSubCategory(),
                place.getDescription(),
                place.getRating(),
                place.getReviewCount(),
                place.getTicketPrice(),
                place.getLocation(),
                place.getImageUrl(),
                place.getIsHidden(),
                reason,
                finalSpeciality
        );
    }

    public String deriveSpeciality(TouristPlace place) {
        if (place == null) return "Solapur regional attraction";
        String name = place.getPlaceName() != null ? place.getPlaceName().toLowerCase(Locale.ROOT) : "";
        if (name.contains("siddheshwar")) {
            return "12th-century lake shrine & historic Gadda Yatra venue";
        } else if (name.contains("bhuikot")) {
            return "Medieval Bahmani ground fort with scenic double moat";
        } else if (name.contains("akkalkot") || name.contains("swami samarth")) {
            return "Sacred pilgrimage center of 19th-century saint Shri Swami Samarth";
        } else if (name.contains("pandharpur") || name.contains("vitthal")) {
            return "Spiritual capital of Maharashtra & holy Varkari pilgrimage hub";
        } else if (name.contains("bustard") || name.contains("maldhok") || name.contains("nannaj")) {
            return "Protected wildlife sanctuary for endangered Great Indian Bustards";
        } else if (name.contains("naldurg")) {
            return "Hydro-architectural fortress featuring the iconic Pani Mahal";
        } else if (name.contains("kambar")) {
            return "Freshwater wetland habitat ideal for birdwatching & peaceful walks";
        } else if (name.contains("kudal sangam")) {
            return "Sacred river confluence with 8th-century Chalukyan rock carvings";
        } else if (name.contains("revan")) {
            return "Hilltop sanctuary with panoramic views over Solapur countryside";
        }
        if (place.getSubCategory() != null && !place.getSubCategory().isBlank()) {
            return place.getSubCategory() + " destination in Solapur district";
        }
        return "Signature " + (place.getCategory() != null ? place.getCategory() : "heritage") + " highlight of Solapur";
    }

    public SolapurGuideResponse getSolapurGuide() {
        List<SolapurGuideResponse.FamousPlaceItem> places = List.of(
                new SolapurGuideResponse.FamousPlaceItem(
                        "Shri Siddheshwar Temple & Lake",
                        "Spiritual & Heritage",
                        "Solapur City Center",
                        "12th-century lake temple dedicated to Lord Siddharameshwar, famous for the annual Gadda Yatra festival.",
                        "Built in the middle of a scenic freshwater lake, this is the prime spiritual and cultural identity of Solapur. It features intricate stone mandapas and evening lakeside lighting.",
                        "/images/Siddheshwar_temple_02.jpg.jpeg",
                        "05:00 AM - 09:30 PM",
                        "Free Entry"
                ),
                new SolapurGuideResponse.FamousPlaceItem(
                        "Great Indian Bustard Sanctuary (Nannaj)",
                        "Wildlife & Nature",
                        "Nannaj, 22 km from Solapur",
                        "One of Asia's few dedicated conservation habitats for the critically endangered Great Indian Bustard (Maldhok).",
                        "A sprawling grassland biome home to the majestic Great Indian Bustard, blackbuck herds, Indian foxes, and diverse grassland avifauna. Best visited between October and February.",
                        "/images/Kambar_Talav_01.JPG.jpeg",
                        "06:30 AM - 06:00 PM",
                        "₹30 - ₹50 per person"
                ),
                new SolapurGuideResponse.FamousPlaceItem(
                        "Shri Swami Samarth Maharaj Mandir (Akkalkot)",
                        "Spiritual Pilgrimage",
                        "Akkalkot, 40 km from Solapur",
                        "Sacred Samadhi shrine of the 19th-century Dattatreya incarnation Shri Swami Samarth Maharaj.",
                        "Revered by millions of devotees across the globe. Features the holy banyan tree (Vatavruksha) where the saint meditated, modern darshan halls, and free Mahaprasad dining.",
                        "/images/SwamiSamarthTemple.avif",
                        "05:00 AM - 10:00 PM",
                        "Free Entry"
                ),
                new SolapurGuideResponse.FamousPlaceItem(
                        "Shri Vitthal Rukmini Mandir (Pandharpur)",
                        "Holy Pilgrimage Hub",
                        "Pandharpur, 70 km from Solapur",
                        "The supreme spiritual nucleus of Maharashtra, located along the holy Chandrabhaga river.",
                        "Center of the multi-century Varkari tradition and annual Pandharpur Wari pilgrimage. Famous for the brick upon which Lord Vitthal stands awaiting devotees.",
                        "/images/VI=itthal rukmini mandir.avif",
                        "04:00 AM - 11:00 PM",
                        "Free Entry"
                ),
                new SolapurGuideResponse.FamousPlaceItem(
                        "Solapur Bhuikot Fort",
                        "Historical Architecture",
                        "Killa Road, Solapur City",
                        "Rare ground-level Islamic and Hindu medieval fortress with a massive dual water moat.",
                        "Constructed during Bahmani sultanate rule and upgraded by Adil Shahis. Houses ancient canons, bastion ramparts, and manicured municipal gardens beside Siddheshwar Lake.",
                        "/images/bhuikot.webp",
                        "09:00 AM - 06:00 PM",
                        "₹25 per person"
                ),
                new SolapurGuideResponse.FamousPlaceItem(
                        "Naldurg Fort & Pani Mahal",
                        "Historical Fortress",
                        "Naldurg, Solapur-Hyderabad Highway",
                        "Massive hydro-fortress featuring the architectural marvel Pani Mahal built into the river dam.",
                        "Built over the Bori River, this engineering masterpiece features an enclosed underwater royal pavilion with water flowing over its roof during monsoon floods.",
                        "/images/Naldurg.jpg.jpeg",
                        "09:00 AM - 05:30 PM",
                        "₹20 per person"
                )
        );

        List<SolapurGuideResponse.FamousFoodItem> foods = List.of(
                new SolapurGuideResponse.FamousFoodItem(
                        "Solapuri Shenga Chutney",
                        "सोलापुरी शेंगदाणा चटणी",
                        "Iconic Dry Chutney",
                        "World-famous dry roasted peanut chutney blended with crushed red chillies, fragrant garlic, cumin, and sea salt. It holds Geographical Indication (GI) significance.",
                        "Crisp, savory, and with a slow-burning smoky heat that elevates any meal.",
                        "Hot Jowar Bhakri, curd, chapati, or warm steamed rice with pure ghee.",
                        "Navi Peth retail shops, traditional Khanavalis, and local Solapur spice markets.",
                        "/images/shenga_chutney.jpg"
                ),
                new SolapurGuideResponse.FamousFoodItem(
                        "Solapuri Jowar Bhakri & Pitla",
                        "ज्वारीची भाकरी आणि पिठलं",
                        "Traditional Staple Food",
                        "Ultra-thin, hand-patted unleavened flatbread crafted from locally grown Maldandi white sorghum, served hot with spiced besan gram-flour gravy.",
                        "Gluten-free, highly nutritious, and rich in natural dietary fiber.",
                        "Shenga Chutney, roasted Kharada (thecha), spring onions, and buttermilk.",
                        "Lingayat Khanavalis, rural dhabas across Solapur, and local authentic eateries.",
                        "/images/jowar_bhakri.jpg"
                ),
                new SolapurGuideResponse.FamousFoodItem(
                        "Shenga Poli (Sweet Peanut Roti)",
                        "शेंगदाणा पोळी",
                        "Festive Sweet Delicacy",
                        "Traditional Maharashtrian sweet flatbread stuffed with finely ground roasted peanuts, organic jaggery, cardamom, and nutmeg.",
                        "Rich in natural sweetness with a delightful nutty aroma and long shelf life.",
                        "Dollop of homemade butter (loni) or warm desi cow ghee.",
                        "Local sweet marts around Siddheshwar Temple and Solapur old town markets.",
                        "/images/shenga_poli.jpg"
                ),
                new SolapurGuideResponse.FamousFoodItem(
                        "Solapuri Kharada (Thecha)",
                        "सोलापुरी खर्डा / मिरचीचा ठेचा",
                        "Spicy Rustic Condiment",
                        "Fiery, coarsely pounded paste made from tawa-roasted green chillies, whole garlic cloves, roasted cumin, and cold-pressed peanut oil.",
                        "Provides an authentic rustic kick that is unforgettable for spice lovers.",
                        "Fresh Jowar Bhakri, raw onion rings, and cooling curd.",
                        "Every traditional restaurant, rural eatery, and roadside meal center in Solapur.",
                        "/images/kharada_thecha.jpg"
                ),
                new SolapurGuideResponse.FamousFoodItem(
                        "Lingayat Khanavali Veg Thali",
                        "लिंगायत खानावळ थाळी",
                        "Wholesome Traditional Feast",
                        "All-you-can-eat pure vegetarian thali featuring limitless Jowar Bhakris, Shengdana chutney, two seasonal vegetable curries, dal, rice, curd, and papad.",
                        "Warm hospitality, completely organic preparation, and affordable comfort dining.",
                        "Solapuri spiced buttermilk (taak) and sweet gulab jamun or sheng poli.",
                        "Basaveshwar Khanavali, Shri Siddheshwar Bhojanalaya, and Navi Peth eateries.",
                        "/images/khanavali_thali.jpg"
                )
        );

        List<SolapurGuideResponse.DistrictSpecialityItem> specialities = List.of(
                new SolapurGuideResponse.DistrictSpecialityItem(
                        "Solapuri Chaddars (GI Tagged)",
                        "Handloom & Textiles",
                        "Solapur is world-famous for its distinct jacquard-woven cotton bedsheets with vibrant geometric motifs, officially granted Geographical Indication (GI) status in 2005.",
                        "Centuries of textile weaving heritage making Solapur the textile hub of Maharashtra.",
                        "Visit cooperative weaving societies and handloom emporiums in Navi Peth and Murarji Peth for genuine authentic pieces.",
                        "bed"
                ),
                new SolapurGuideResponse.DistrictSpecialityItem(
                        "Solapuri Terry Towels & Fabrics",
                        "Cotton Textiles",
                        "Celebrated for dense absorbent pile loops, durable color-fast dyeing, and premium pure cotton construction supplied across India and exported globally.",
                        "Major economic pillar employing thousands of traditional weaver families.",
                        "Available at factory outlet rates near the Solapur MIDC textile zone and city wholesale markets.",
                        "dry_cleaning"
                ),
                new SolapurGuideResponse.DistrictSpecialityItem(
                        "Gadda Yatra Festival",
                        "Cultural Festival & Fair",
                        "The grand 3-day annual festival celebrated every Makar Sankranti (January) in honor of Saint Siddharameshwar, witnessing colorful Kathi processions.",
                        "Unites people of all communities with traditional fairgrounds, cultural dances, and sacred oil lamp lighting.",
                        "Plan your trip around January 12-16 to witness the majestic carnival and temple illuminations.",
                        "celebration"
                ),
                new SolapurGuideResponse.DistrictSpecialityItem(
                        "Navi Peth & Traditional Bazaars",
                        "Shopping & Culture",
                        "The bustling heart of Solapur city commerce, filled with textile stores, spice merchants selling freshly pounded Shenga Chutney, and traditional jewellery.",
                        "Vibrant street culture where travelers can explore local Maharashtrian lifestyle and authentic souvenirs.",
                        "Evening walks between 5 PM and 9 PM are the best time to experience street snacks and souvenir shopping.",
                        "shopping_bag"
                )
        );

        return new SolapurGuideResponse(places, foods, specialities);
    }
}
