/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2022 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.lora.codec.twtg.neon.javascript.engine.graaljs;


import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.AbstractPoolingTargetSource;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraalJavaScriptEnginePoolFactory {

    @Value("${javascript.engine.pool.size.max}")
    private int enginePoolMaxSize;

    /**
     * Returns the Object pool containing JavaScriptEngine beans.
     *
     * @return the pool
     */
    @Bean
    public TargetSource javaScriptEngineSource() {
        final AbstractPoolingTargetSource poolingConfig = new CommonsPool2TargetSource();
        poolingConfig.setMaxSize(enginePoolMaxSize);

        // The targetBeanName is mandatory
        poolingConfig.setTargetBeanName(GraalJavaScriptEngine.GRAAL_JAVA_SCRIPT_ENGINE_BEAN_NAME);

        return poolingConfig;
    }

    /**
     * Returns a ProxyFactoryBean that is correctly pooled.
     *
     * @return the proxy JavaScriptEngine bean which we autowire and use
     */
    @Bean
    public ProxyFactoryBean graalJavaScriptEngineProxy(TargetSource javaScriptEngineSource) {
        ProxyFactoryBean proxyBean = new ProxyFactoryBean();
        proxyBean.setTargetSource(javaScriptEngineSource);

        return proxyBean;
    }
}