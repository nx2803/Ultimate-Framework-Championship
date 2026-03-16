package com.example.ufcback.config;

import com.example.ufcback.domain.User;
import com.example.ufcback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final com.example.ufcback.repository.TechListRepository techListRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 관리자 계정 생성
        initAdminUser();
        // 기술 스택 초기화 (75개)
        initTechList();
    }

    private void initAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123!"))
                    .role("ROLE_ADMIN")
                    .build();
            userRepository.save(admin);
            log.info("Default Admin User created");
        }
    }

    private void initTechList() {
        if (techListRepository.count() > 0) return;

        log.info("Starting to initialize 75 tech stacks with brand identities...");
        
        // LANGUAGE (16)
        saveTech("JavaScript", "LANGUAGE", "javascript", "nodejs/node", "#F7DF1E", "https://cdn.simpleicons.org/javascript");
        saveTech("TypeScript", "LANGUAGE", "typescript", "microsoft/typescript", "#3178C6", "https://cdn.simpleicons.org/typescript");
        saveTech("Python", "LANGUAGE", "python", "python/cpython", "#3776AB", "https://cdn.simpleicons.org/python");
        saveTech("Java", "LANGUAGE", "java", "openjdk/jdk", "#007396", "https://cdn.simpleicons.org/openjdk");
        saveTech("Rust", "LANGUAGE", "rust", "rust-lang/rust", "#000000", "https://cdn.simpleicons.org/rust");
        saveTech("Go", "LANGUAGE", "go", "golang/go", "#00ADD8", "https://cdn.simpleicons.org/go");
        saveTech("Kotlin", "LANGUAGE", "kotlin", "jetBrains/kotlin", "#7F52FF", "https://cdn.simpleicons.org/kotlin");
        saveTech("Swift", "LANGUAGE", "swift", "apple/swift", "#F05138", "https://cdn.simpleicons.org/swift");
        saveTech("C#", "LANGUAGE", "csharp", "dotnet/csharplang", "#239120", "https://cdn.simpleicons.org/csharp");
        saveTech("PHP", "LANGUAGE", "php", "php/php-src", "#777BB4", "https://cdn.simpleicons.org/php");
        saveTech("Ruby", "LANGUAGE", "ruby", "ruby/ruby", "#CC342D", "https://cdn.simpleicons.org/ruby");
        saveTech("Dart", "LANGUAGE", "dart", "dart-lang/sdk", "#0175C2", "https://cdn.simpleicons.org/dart");
        saveTech("Scala", "LANGUAGE", "scala", "scala/scala", "#DC322F", "https://cdn.simpleicons.org/scala");
        saveTech("Elixir", "LANGUAGE", "elixir", "elixir-lang/elixir", "#4E275E", "https://cdn.simpleicons.org/elixir");
        saveTech("Zig", "LANGUAGE", "zig", "ziglang/zig", "#F7A41D", "https://cdn.simpleicons.org/zig");
        saveTech("C++", "LANGUAGE", "cpp", "isocpp/cppcoreguidelines", "#00599C", "https://cdn.simpleicons.org/cplusplus");

        // FRONTEND (12)
        saveTech("React", "FRONTEND", "react", "facebook/react", "#61DAFB", "https://cdn.simpleicons.org/react");
        saveTech("Vue", "FRONTEND", "vue", "vuejs/core", "#4FC08D", "https://cdn.simpleicons.org/vuedotjs");
        saveTech("Angular", "FRONTEND", "angular", "angular/angular", "#DD0031", "https://cdn.simpleicons.org/angular");
        saveTech("Svelte", "FRONTEND", "svelte", "sveltejs/svelte", "#FF3E00", "https://cdn.simpleicons.org/svelte");
        saveTech("Solid", "FRONTEND", "solid", "solidjs/solid", "#2C4F7C", "https://cdn.simpleicons.org/solid");
        saveTech("Next.js", "FRONTEND", "nextjs", "vercel/next.js", "#000000", "https://cdn.simpleicons.org/nextdotjs");
        saveTech("Nuxt", "FRONTEND", "nuxt", "nuxt/nuxt", "#00DC82", "https://cdn.simpleicons.org/nuxtdotjs");
        saveTech("Astro", "FRONTEND", "astro", "withastro/astro", "#BC52EE", "https://cdn.simpleicons.org/astro");
        saveTech("Remix", "FRONTEND", "remix", "remix-run/remix", "#000000", "https://cdn.simpleicons.org/remix");
        saveTech("Qwik", "FRONTEND", "qwik", "BuilderIO/qwik", "#16B4FB", "https://cdn.simpleicons.org/qwik");
        saveTech("Alpine.js", "FRONTEND", "alpinejs", "alpinejs/alpine", "#8BC0D0", "https://cdn.simpleicons.org/alpinedotjs");
        saveTech("Preact", "FRONTEND", "preact", "preactjs/preact", "#673AB7", "https://cdn.simpleicons.org/preact");

        // BACKEND (18)
        saveTech("Spring", "BACKEND", "spring", "spring-projects/spring-framework", "#6DB33F", "https://cdn.simpleicons.org/spring");
        saveTech("Django", "BACKEND", "django", "django/django", "#092E20", "https://cdn.simpleicons.org/django");
        saveTech("Laravel", "BACKEND", "laravel", "laravel/laravel", "#FF2D20", "https://cdn.simpleicons.org/laravel");
        saveTech("Express", "BACKEND", "express", "expressjs/express", "#000000", "https://cdn.simpleicons.org/express");
        saveTech("NestJS", "BACKEND", "nestjs", "nestjs/nest", "#E0234E", "https://cdn.simpleicons.org/nestjs");
        saveTech("FastAPI", "BACKEND", "fastapi", "tiangolo/fastapi", "#05998B", "https://cdn.simpleicons.org/fastapi");
        saveTech("Rails", "BACKEND", "rails", "rails/rails", "#CC0000", "https://cdn.simpleicons.org/rubyonrails");
        saveTech("Flask", "BACKEND", "flask", "pallets/flask", "#000000", "https://cdn.simpleicons.org/flask");
        saveTech("Gin", "BACKEND", "gin", "gin-gonic/gin", "#00ADD8", "https://cdn.simpleicons.org/go");
        saveTech("Fiber", "BACKEND", "fiber", "gofiber/fiber", "#00ADD8", "https://cdn.simpleicons.org/go");
        saveTech("Hono", "BACKEND", "hono", "honojs/hono", "#E3642F", "https://cdn.simpleicons.org/hono");
        saveTech("Phoenix", "BACKEND", "phoenix", "phoenixframework/phoenix", "#FD4F00", "https://cdn.simpleicons.org/phoenixframework");
        saveTech("Actix", "BACKEND", "actix", "actix/actix-web", "#000000", "https://cdn.simpleicons.org/rust");
        saveTech("Axum", "BACKEND", "axum", "tokio-rs/axum", "#000000", "https://cdn.simpleicons.org/rust");
        saveTech("Quarkus", "BACKEND", "quarkus", "quarkusio/quarkus", "#4695EB", "https://cdn.simpleicons.org/quarkus");
        saveTech("Micronaut", "BACKEND", "micronaut", "micronaut-projects/micronaut-core", "#007D8A", "https://cdn.simpleicons.org/micronaut");
        saveTech("Ktor", "BACKEND", "ktor", "ktorio/ktor", "#000000", "https://cdn.simpleicons.org/ktor");
        saveTech("Rocket", "BACKEND", "rocket", "rwf2/Rocket", "#000000", "https://cdn.simpleicons.org/rust");

        // MOBILE (4) - [Rest truncated for brevity, same pattern]
        saveTech("Flutter", "MOBILE", "flutter", "flutter/flutter", "#02569B", "https://cdn.simpleicons.org/flutter");
        saveTech("React Native", "MOBILE", "react-native", "facebook/react-native", "#61DAFB", "https://cdn.simpleicons.org/react");
        saveTech("Jetpack Compose", "MOBILE", "jetpack-compose", "google/accompanist", "#4285F4", "https://cdn.simpleicons.org/android");
        saveTech("Expo", "MOBILE", "expo", "expo/expo", "#000020", "https://cdn.simpleicons.org/expo");

        // INFRA (8)
        saveTech("Docker", "INFRA", "docker", "docker/cli", "#2496ED", "https://cdn.simpleicons.org/docker");
        saveTech("Kubernetes", "INFRA", "kubernetes", "kubernetes/kubernetes", "#326CE5", "https://cdn.simpleicons.org/kubernetes");
        saveTech("Terraform", "INFRA", "terraform", "hashicorp/terraform", "#7B42BC", "https://cdn.simpleicons.org/terraform");
        saveTech("Ansible", "INFRA", "ansible", "ansible/ansible", "#EE0000", "https://cdn.simpleicons.org/ansible");
        saveTech("Helm", "INFRA", "helm", "helm/helm", "#0F1689", "https://cdn.simpleicons.org/helm");
        saveTech("ArgoCD", "INFRA", "argocd", "argoproj/argo-cd", "#EF7B4D", "https://cdn.simpleicons.org/argo");
        saveTech("Jenkins", "INFRA", "jenkins", "jenkinsci/jenkins", "#D24939", "https://cdn.simpleicons.org/jenkins");
        saveTech("GitHub Actions", "INFRA", "github-actions", "actions/runner", "#2088FF", "https://cdn.simpleicons.org/githubactions");

        // DATABASE (10)
        saveTech("GraphQL", "DATABASE", "graphql", "graphql/graphql-js", "#E10098", "https://cdn.simpleicons.org/graphql");
        saveTech("Prisma", "DATABASE", "prisma", "prisma/prisma", "#2D3748", "https://cdn.simpleicons.org/prisma");
        saveTech("Supabase", "DATABASE", "supabase", "supabase/supabase", "#3ECF8E", "https://cdn.simpleicons.org/supabase");
        saveTech("Firebase", "DATABASE", "firebase", "firebase/firebase-js-sdk", "#FFCA28", "https://cdn.simpleicons.org/firebase");
        saveTech("Redis", "DATABASE", "redis", "redis/redis", "#DC382D", "https://cdn.simpleicons.org/redis");
        saveTech("MongoDB", "DATABASE", "mongodb", "mongodb/mongo", "#47A248", "https://cdn.simpleicons.org/mongodb");
        saveTech("PostgreSQL", "DATABASE", "postgresql", "postgres/postgres", "#4169E1", "https://cdn.simpleicons.org/postgresql");
        saveTech("MySQL", "DATABASE", "mysql", "mysql/mysql-server", "#4479A1", "https://cdn.simpleicons.org/mysql");
        saveTech("Drizzle", "DATABASE", "drizzle", "drizzle-team/drizzle-orm", "#C5F74F", "https://cdn.simpleicons.org/drizzle");
        saveTech("TypeORM", "DATABASE", "typeorm", "typeorm/typeorm", "#262626", "https://cdn.simpleicons.org/typeorm");

        // AI_ML (7)
        saveTech("TensorFlow", "AI_ML", "tensorflow", "tensorflow/tensorflow", "#FF6F00", "https://cdn.simpleicons.org/tensorflow");
        saveTech("PyTorch", "AI_ML", "pytorch", "pytorch/pytorch", "#EE4C2C", "https://cdn.simpleicons.org/pytorch");
        saveTech("LangChain", "AI_ML", "langchain", "langchain-ai/langchain", "#000000", "https://cdn.simpleicons.org/langchain");
        saveTech("Hugging Face", "AI_ML", "huggingface", "huggingface/transformers", "#FFD21E", "https://cdn.simpleicons.org/huggingface");
        saveTech("scikit-learn", "AI_ML", "scikit-learn", "scikit-learn/scikit-learn", "#F7931E", "https://cdn.simpleicons.org/scikitlearn");
        saveTech("Keras", "AI_ML", "keras", "keras-team/keras", "#D00000", "https://cdn.simpleicons.org/keras");
        saveTech("JAX", "AI_ML", "jax", "google/jax", "#D00000", "https://cdn.simpleicons.org/google");

        log.info("Finished initializing {} tech stacks", techListRepository.count());
    }

    private void saveTech(String name, String category, String topic, String repo, String color, String logoUrl) {
        techListRepository.save(com.example.ufcback.domain.TechList.builder()
                .name(name)
                .category(category)
                .topicKeyword(topic)
                .primaryRepo(repo)
                .color(color)
                .logoUrl(logoUrl)
                .build());
    }
}
