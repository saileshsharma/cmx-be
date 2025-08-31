package com.cb.th.claims.cmx.config.graphql;

import graphql.schema.idl.TypeRuntimeWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.type(TypeRuntimeWiring.newTypeWiring("Person").typeResolver(env -> {
            Object src = env.getObject();
            if (src instanceof com.cb.th.claims.cmx.entity.insured.Insured) {
                return env.getSchema().getObjectType("Insured");
            }
            if (src instanceof com.cb.th.claims.cmx.entity.surveyor.Surveyor) {
                // If you also want Surveyor to be a Person, return Surveyor here
                return env.getSchema().getObjectType("Surveyor");
            }
            return null;
        })).type(TypeRuntimeWiring.newTypeWiring("Event").typeResolver(env -> env.getSchema().getObjectType("SurveyorLocationEvent")));
    }
}
