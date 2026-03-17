package com.example.ufcback.infrastructure.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubClient {

    private final RestClient restClient;
    private final String githubToken;

    public GitHubClient(
            @Value("${github.api.base-url}") String baseUrl,
            @Value("${github.api.token}") String githubToken) {
        
        this.githubToken = (githubToken != null && !githubToken.contains("${")) ? githubToken : "";
        
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + this.githubToken)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
        
        if (this.githubToken.isEmpty()) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("CRITICAL: GitHub API Token is MISSING!");
            System.err.println("Batch jobs will likely fail with 403 Forbidden.");
            System.err.println("Please check your .env file and GITHUB_TOKEN variable.");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } else {
            String masked = this.githubToken.length() > 8 
                ? this.githubToken.substring(0, 8) + "..." 
                : "****";
            System.out.println("SUCCESS: GitHub API Token loaded (Prefix: " + masked + ")");
        }
    }

    /**
     * Trending repositories (stars > 1000, sorted by stars)
     */
    public String searchTrendingRepositories() {
        var response = restClient.get()
                .uri("/search/repositories?q=stars:>1000&sort=stars&order=desc")
                .retrieve()
                .toEntity(String.class);
        
        logRateLimit(response.getHeaders());
        return response.getBody();
    }

    /**
     * Topic stats (total repository count for a given topic)
     */
    public String getTopicStats(String topic) {
        var response = restClient.get()
                .uri("/search/repositories?q=topic:" + topic + "&per_page=1")
                .retrieve()
                .toEntity(String.class);
        
        logRateLimit(response.getHeaders());
        return response.getBody();
    }

    /**
     * Language stats (total repository count written in a given language)
     * Use this for LANGUAGE category instead of getTopicStats()
     * e.g. language:JavaScript returns repos where JS is the primary language
     */
    public String getLanguageStats(String language) {
        var response = restClient.get()
                .uri("/search/repositories?q=language:" + language + "&per_page=1")
                .retrieve()
                .toEntity(String.class);
        
        logRateLimit(response.getHeaders());
        return response.getBody();
    }

    /**
     * Repository details (stars, forks, etc.)
     */
    public String getRepositoryDetails(String ownerRepo) {
        var response = restClient.get()
                .uri("/repos/" + ownerRepo)
                .retrieve()
                .toEntity(String.class);
        
        logRateLimit(response.getHeaders());
        return response.getBody();
    }

    private void logRateLimit(org.springframework.http.HttpHeaders headers) {
        String remaining = headers.getFirst("X-RateLimit-Remaining");
        String limit = headers.getFirst("X-RateLimit-Limit");
        if (remaining != null && limit != null) {
            System.out.println("GitHub API Rate Limit: " + remaining + "/" + limit);
        }
    }
}
