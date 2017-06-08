/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olestourko.sdbudget.core.dagger;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.inject.Singleton;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;

/**
 *
 * @author oles
 */
@Singleton
@Module
public class ConfigurationModule {

    @Singleton
    @Provides
    public ConfigurationProvider configurationProvider() {
        // Specify which files to load. Configuration from both files will be merged.
        ConfigFilesProvider configFilesProvider = () -> Arrays.asList(Paths.get("./configuration.yaml"));

        // Use local files as configuration store
        ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);

        // Create provider
        return new ConfigurationProviderBuilder()
                .withConfigurationSource(source)
                .build();
    }
}