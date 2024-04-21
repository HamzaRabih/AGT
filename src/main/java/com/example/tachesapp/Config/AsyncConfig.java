package com.example.tachesapp.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
/**
 * Classe de configuration pour la gestion des tâches asynchrones avec Spring.
 */
@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    /**
     * Configure le gestionnaire d'exécution asynchrone.
     *
     * @return Un gestionnaire d'exécution de tâches asynchrones (Executor).
     */
    @Override
    public Executor getAsyncExecutor() {
        // Crée un ThreadPoolTaskExecutor pour l'exécution asynchrone.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Définit la taille du pool principal à 5 threads.
        executor.setCorePoolSize(5);

        // Définit la taille maximale du pool à 10 threads.
        executor.setMaxPoolSize(10);

        // Définit la capacité de la file d'attente à 25.
        executor.setQueueCapacity(25);

        // Initialise le gestionnaire d'exécution.
        executor.initialize();

        // Retourne le gestionnaire d'exécution configuré.
        return executor;
    }
}

