package com.example.weather;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Controller
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }

    // Task B: The Human-Readable Dashboard
    @GetMapping("/")
    public String getDashboard(Model model) {
        // Fetch data for both cities
        JsonNode fresnoData = fetchForecastNode("36.7378", "-119.7871");
        JsonNode nyData = fetchForecastNode("40.7128", "-74.0060");

        // Add them to the Thymeleaf model to be used in HTML
        model.addAttribute("fresno", fresnoData.path("properties").path("periods").get(0));
        model.addAttribute("ny", nyData.path("properties").path("periods").get(0));

        return "dashboard"; // This looks for src/main/resources/templates/dashboard.html
    }

    // Keep the Task A JSON endpoints for testing
    @GetMapping(value = "/weather/fresno", produces = "application/json")
    @ResponseBody
    public String getFresnoRaw() { return fetchForecastNode("36.7378", "-119.7871").toString(); }

    private JsonNode fetchForecastNode(String lat, String lon) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Jose-A-App");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            String pointsUrl = "https://api.weather.gov/points/" + lat + "," + lon;
            ResponseEntity<JsonNode> pointsResponse = restTemplate.exchange(pointsUrl, HttpMethod.GET, entity, JsonNode.class);
            String forecastUrl = pointsResponse.getBody().path("properties").path("forecast").asText();

            return restTemplate.exchange(forecastUrl, HttpMethod.GET, entity, JsonNode.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}