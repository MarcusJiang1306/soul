/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.springboot.starter.client.springmvc;

import org.dromara.soul.client.core.register.SoulClientRegisterRepositoryFactory;
import org.dromara.soul.client.springmvc.init.ContextRegisterListener;
import org.dromara.soul.client.springmvc.init.SpringMvcClientBeanPostProcessor;
import org.dromara.soul.register.client.api.SoulClientRegisterRepository;
import org.dromara.soul.register.common.config.SoulRegisterCenterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Soul client http configuration.
 *
 * @author xiaoyu
 */
@Configuration
public class SoulSpringMvcClientConfiguration {
    
    /**
     * Register the register repository for http client bean post processor.
     *
     * @param config the config
     * @return the client register repository
     */
    @Bean
    public SoulClientRegisterRepository soulClientRegisterRepository(final SoulRegisterCenterConfig config) {
        return SoulClientRegisterRepositoryFactory.newInstance(config);
    }
    
    /**
     * Spring http client bean post processor .
     *
     * @param config the config
     * @param soulClientRegisterRepository the client register repository
     * @return the spring http client bean post processor
     */
    @Bean
    public SpringMvcClientBeanPostProcessor springHttpClientBeanPostProcessor(final SoulRegisterCenterConfig config, final SoulClientRegisterRepository soulClientRegisterRepository) {
        return new SpringMvcClientBeanPostProcessor(config, soulClientRegisterRepository);
    }
    
    /**
     * Context register listener context register listener.
     *
     * @param config the config
     * @return the context register listener
     */
    @Bean
    public ContextRegisterListener contextRegisterListener(final SoulRegisterCenterConfig config) {
        return new ContextRegisterListener(config);
    }
    
    /**
     * Soul Register Center Config.
     *
     * @return the Register Center Config
     */
    @Bean
    @ConfigurationProperties(prefix = "soul.client")
    public SoulRegisterCenterConfig soulRegisterCenterConfig() {
        return new SoulRegisterCenterConfig();
    }
}
