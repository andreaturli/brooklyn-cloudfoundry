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
package org.apache.brooklyn.karaf.itests;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFileExtend;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.File;

import javax.inject.Inject;

import org.apache.karaf.features.FeaturesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

@RunWith(PaxExam.class)
//@ExamReactorStrategy(PerClass.class)
public class FeatureInstallationTest extends TestBase {

    @Inject
    FeaturesService featuresService;

    @Test
    public void testBrooklynLocationsCloudfoundryFeature() throws Exception {
        featuresService.addRepository(getFeaturesFile().toURI());
        featuresService.installFeature("brooklyn-location-cloudfoundry");
        assertTrue(featuresService.isInstalled(featuresService.getFeature("brooklyn-location-cloudfoundry")));
    }

    @Configuration
    public Option[] config() {
        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.apache.karaf")
                .artifactId("apache-karaf")
                .versionAsInProject()
                .type("tar.gz");
        return new Option[]{
                karafDistributionConfiguration().frameworkUrl(karafUrl).name("Apache Karaf").unpackDirectory(new File("target/exam")),
                //configureSecurity().disableKarafMBeanServerBuilder(),
//                keepRuntimeFolder(),
                editConfigurationFilePut("etc/system.properties", "features.xml", System.getProperty("features.xml")),
                editConfigurationFileExtend(
                    "etc/org.ops4j.pax.url.mvn.cfg",
                    "org.ops4j.pax.url.mvn.repositories",
                    "file:"+System.getProperty("features.repo")+"@id=local@snapshots@releases"),
                logLevel(LogLevelOption.LogLevel.DEBUG), junitBundles()
        };
    }
}