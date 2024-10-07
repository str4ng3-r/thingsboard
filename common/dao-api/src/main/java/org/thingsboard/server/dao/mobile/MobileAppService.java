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
package org.thingsboard.server.dao.mobile;

import org.thingsboard.server.common.data.id.MobileAppBundleId;
import org.thingsboard.server.common.data.id.MobileAppId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.mobile.AndroidQrCodeConfig;
import org.thingsboard.server.common.data.mobile.IosQrCodeConfig;
import org.thingsboard.server.common.data.mobile.MobileApp;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

public interface MobileAppService extends EntityDaoService {

    MobileApp saveMobileApp(TenantId tenantId, MobileApp mobileApp);

    void deleteMobileAppById(TenantId tenantId, MobileAppId mobileAppId);

    MobileApp findMobileAppById(TenantId tenantId, MobileAppId mobileAppId);

    PageData<MobileApp> findMobileAppsByTenantId(TenantId tenantId, PageLink pageLink);

    AndroidQrCodeConfig findAndroidQrCodeConfig(TenantId tenantId, MobileAppBundleId mobileAppBundleId);

    IosQrCodeConfig findIosQrCodeConfig(TenantId tenantId, MobileAppBundleId mobileAppBundleId);

    void deleteMobileAppsByTenantId(TenantId tenantId);

}
