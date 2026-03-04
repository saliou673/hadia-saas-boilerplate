package com.hadiasaas.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * AWS client configuration used by the S3 storage strategy.
 */
@Configuration
public class AwsStorageConfiguration {

    @Bean
    @Conditional(AwsStoragePropertiesPresentCondition.class)
    S3Client s3Client(ApplicationProperties applicationProperties) {
        ApplicationProperties.Storage.Aws aws = applicationProperties.getStorage().aws();

        S3ClientBuilder builder = S3Client.builder().region(Region.of(aws.region()));

        if (StringUtils.isNotBlank(aws.endpoint())) {
            builder = builder.endpointOverride(URI.create(aws.endpoint()));
        }

        if (aws.pathStyleAccessEnabled()) {
            builder = builder.serviceConfiguration(
                    S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build()
            );
        }

        return builder.build();
    }

    static final class AwsStoragePropertiesPresentCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String region = context.getEnvironment().getProperty("app.storage.aws.region");
            String bucket = context.getEnvironment().getProperty("app.storage.aws.bucket");
            return StringUtils.isNotBlank(region) && StringUtils.isNotBlank(bucket);
        }
    }
}
