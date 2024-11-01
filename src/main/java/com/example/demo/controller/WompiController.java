package com.example.demo.controller;

import com.example.demo.persistence.entity.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/wompi")
public class WompiController {

    @Value("${wompi.clientId}")
    private String clientId;

    @Value("${wompi.clientSecret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    @Autowired
    public WompiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/getToken")
    public ResponseEntity<String> getToken() {
        String url = "https://id.wompi.sv/connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("audience", "wompi_api");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Extraer solo el access_token
        String token = response.getBody();
        // parsear el JSON para obtener el token, usando una biblioteca como Jackson o Gson

        return ResponseEntity.ok(token);
    }


    @PostMapping("/createTransaction")
    public ResponseEntity<String> createTransaction(@RequestHeader("Authorization") String bearerToken,
                                                    @RequestBody TransactionRequest transactionRequest) {
        String url = "https://api.wompi.sv/TransaccionCompra/3Ds";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String accessToken = bearerToken.startsWith("Bearer ") ? bearerToken : "Bearer " + bearerToken;
        headers.set("Authorization", accessToken); // Usar el token como Bearer

        HttpEntity<TransactionRequest> entity = new HttpEntity<>(transactionRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Verifica si la respuesta es exitosa
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                // Retornar respuesta de error de la API
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Manejo de errores específicos de HTTP
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // Manejo de errores generales
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

}
