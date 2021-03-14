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

package org.dromara.soul.register.client.consul;

import com.ecwid.consul.v1.kv.KeyValueClient;
import lombok.extern.slf4j.Slf4j;
import org.dromara.soul.common.enums.RpcTypeEnum;
import org.dromara.soul.common.utils.GsonUtils;
import org.dromara.soul.register.client.api.SoulClientRegisterRepository;
import org.dromara.soul.register.common.dto.MetaDataRegisterDTO;
import org.dromara.soul.register.common.dto.URIRegisterDTO;
import org.dromara.soul.register.common.path.ZkRegisterPathConstants;
import org.dromara.soul.spi.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;

@Join
@Slf4j
public class ConsulClientRegisterRepository implements SoulClientRegisterRepository {

    @Autowired
    private ConsulRegistration consulRegistration;

    @Autowired
    private KeyValueClient keyValueClient;

    @Override
    public void persistInterface(MetaDataRegisterDTO metadata) {
        String rpcType = metadata.getRpcType();
        String contextPath = metadata.getContextPath().substring(1);
        registerMetadata(rpcType, contextPath, metadata);
        if (RpcTypeEnum.HTTP.getName().equals(rpcType) || RpcTypeEnum.TARS.getName().equals(rpcType) || RpcTypeEnum.GRPC.getName().equals(rpcType)) {
            registerURI(rpcType, contextPath, metadata);
        }
        log.info("{} Consul client register success: {}", rpcType, metadata.toString());
    }

    private void registerMetadata(String rpcType, String contextPath, MetaDataRegisterDTO metadata) {
        String metadataNodeName = buildMetadataNodeName(metadata);
        String metaDataPath = ZkRegisterPathConstants.buildMetaDataParentPath(rpcType, contextPath);

        String realNode = ZkRegisterPathConstants.buildRealNode(metaDataPath, metadataNodeName);
        String metadataJson = GsonUtils.getInstance().toJson(metadata);
        keyValueClient.setKVValue(realNode, metadataJson);
    }

    private void registerURI(String rpcType, String contextPath, MetaDataRegisterDTO metadata) {
        URIRegisterDTO uriRegisterDTO = URIRegisterDTO.transForm(metadata);
        consulRegistration.getService().getMeta().put("uri", GsonUtils.getInstance().toJson(uriRegisterDTO));
    }

    private String buildMetadataNodeName(final MetaDataRegisterDTO metadata) {
        String nodeName;
        String rpcType = metadata.getRpcType();
        if (RpcTypeEnum.HTTP.getName().equals(rpcType) || RpcTypeEnum.SPRING_CLOUD.getName().equals(rpcType)) {
            nodeName = String.join("-", metadata.getContextPath(), metadata.getRuleName().replace("/", "-"));
        } else {
            nodeName = buildNodeName(metadata.getServiceName(), metadata.getMethodName());
        }
        return nodeName.substring(1);
    }

    private String buildNodeName(final String serviceName, final String methodName) {
        return String.join("#", serviceName, methodName);
    }
}
