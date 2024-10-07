/**
 * Copyright © 2016-2024 The Thingsboard Authors
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
package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.AndroidQrCodeConfig;
import org.thingsboard.server.common.data.mobile.IosQrCodeConfig;
import org.thingsboard.server.common.data.mobile.MobileApp;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.service.DaoSqlTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DaoSqlTest
public class MobileAppControllerTest extends AbstractControllerTest {

    static final TypeReference<PageData<MobileApp>> PAGE_DATA_MOBILE_APP_TYPE_REF = new TypeReference<>() {
    };

    @Before
    public void setUp() throws Exception {
        loginSysAdmin();
    }

    @After
    public void tearDown() throws Exception {
        PageData<MobileApp> pageData = doGetTypedWithPageLink("/api/mobile/app?", PAGE_DATA_MOBILE_APP_TYPE_REF, new PageLink(10, 0));
        for (MobileApp mobileApp : pageData.getData()) {
            doDelete("/api/mobile/app/" + mobileApp.getId().getId())
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void testSaveMobileApp() throws Exception {
        PageData<MobileApp> pageData = doGetTypedWithPageLink("/api/mobile/app?", PAGE_DATA_MOBILE_APP_TYPE_REF, new PageLink(10, 0));
        assertThat(pageData.getData()).isEmpty();

        MobileApp mobileApp = validMobileApp(TenantId.SYS_TENANT_ID, "my.test.package");
        MobileApp savedMobileApp = doPost("/api/mobile/app", mobileApp, MobileApp.class);

        PageData<MobileApp> pageData2 = doGetTypedWithPageLink("/api/mobile/app?", PAGE_DATA_MOBILE_APP_TYPE_REF, new PageLink(10, 0));
        assertThat(pageData2.getData()).hasSize(1);
        assertThat(pageData2.getData().get(0)).isEqualTo(savedMobileApp);

        MobileApp retrievedMobileAppInfo = doGet("/api/mobile/app/{id}", MobileApp.class, savedMobileApp.getId().getId());
        assertThat(retrievedMobileAppInfo).isEqualTo(savedMobileApp);

        doDelete("/api/mobile/app/" + savedMobileApp.getId().getId());
        doGet("/api/mobile/app/{id}", savedMobileApp.getId().getId())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveMobileAppWithShortAppSecret() throws Exception {
        MobileApp mobileApp = validMobileApp(TenantId.SYS_TENANT_ID, "mobileApp.ce");
        mobileApp.setAppSecret("short");
        doPost("/api/mobile/app", mobileApp)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("appSecret must be at least 16 and max 2048 characters")));
    }

    @Test
    public void testShouldNotSaveMobileAppWithWrongQrCodeConf() throws Exception {
        MobileApp mobileApp = validMobileApp(TenantId.SYS_TENANT_ID, "mobileApp.ce");
        AndroidQrCodeConfig androidQrCodeConfig = AndroidQrCodeConfig.builder()
                .enabled(true)
                .appPackage(null)
                .sha256CertFingerprints(null)
                .build();
        mobileApp.setQrCodeConfig(androidQrCodeConfig);

        doPost("/api/mobile/app", mobileApp)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Validation error: appPackage must not be blank, sha256CertFingerprints must not be blank, storeLink must not be blank")));

        androidQrCodeConfig.setAppPackage("test_app_package");
        doPost("/api/mobile/app", mobileApp)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Validation error: sha256CertFingerprints must not be blank, storeLink must not be blank")));

        androidQrCodeConfig.setSha256CertFingerprints("test_sha_256");
        androidQrCodeConfig.setStoreLink("https://store.com");
        doPost("/api/mobile/app", mobileApp)
                .andExpect(status().isOk());
    }

    @Test
    public void testShouldNotSaveMobileAppWithWrongIosConf() throws Exception {
        MobileApp mobileApp = validMobileApp(TenantId.SYS_TENANT_ID, "mobileApp.ce");
        IosQrCodeConfig iosQrCodeConfig = IosQrCodeConfig.builder()
                .enabled(true)
                .appId(null)
                .build();
        mobileApp.setQrCodeConfig(iosQrCodeConfig);

        doPost("/api/mobile/app", mobileApp)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Validation error: appId must not be blank, storeLink must not be blank")));

        iosQrCodeConfig.setAppId("test_app_id");
        iosQrCodeConfig.setStoreLink("https://store.com");
        doPost("/api/mobile/app", mobileApp)
                .andExpect(status().isOk());
    }

    private MobileApp validMobileApp(TenantId tenantId, String mobileAppName) {
        MobileApp MobileApp = new MobileApp();
        MobileApp.setTenantId(tenantId);
        MobileApp.setPkgName(mobileAppName);
        MobileApp.setAppSecret(StringUtils.randomAlphanumeric(24));
        return MobileApp;
    }

}
