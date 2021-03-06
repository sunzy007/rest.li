/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.linkedin.restli.internal.server.methods.response;


import com.linkedin.jersey.api.uri.UriBuilder;
import com.linkedin.jersey.api.uri.UriComponent;
import com.linkedin.r2.message.rest.RestRequest;
import com.linkedin.restli.common.IdResponse;
import com.linkedin.restli.common.ProtocolVersion;
import com.linkedin.restli.common.RestConstants;
import com.linkedin.restli.internal.common.URIParamUtils;
import com.linkedin.restli.internal.server.AugmentedRestLiResponseData;
import com.linkedin.restli.internal.server.RoutingResult;
import com.linkedin.restli.internal.server.ServerResourceContext;
import com.linkedin.restli.server.CreateResponse;

import java.util.Map;


public class CreateResponseBuilder implements RestLiResponseBuilder
{
  @Override
  public PartialRestResponse buildResponse(RoutingResult routingResult, AugmentedRestLiResponseData responseData)
  {
    return new PartialRestResponse.Builder().entity(responseData.getEntityResponse())
                                            .headers(responseData.getHeaders()).status(responseData.getStatus())
                                            .build();
  }

  @Override
  public AugmentedRestLiResponseData buildRestLiResponseData(RestRequest request, RoutingResult routingResult,
                                                             Object result, Map<String, String> headers)
  {
    CreateResponse createResponse = (CreateResponse) result;
    if (createResponse.hasId())
    {
      final ProtocolVersion protocolVersion = ((ServerResourceContext) routingResult.getContext()).getRestliProtocolVersion();
      String stringKey = URIParamUtils.encodeKeyForUri(createResponse.getId(), UriComponent.Type.PATH_SEGMENT, protocolVersion);
      UriBuilder uribuilder = UriBuilder.fromUri(request.getURI());
      uribuilder.path(stringKey);
      headers.put(RestConstants.HEADER_LOCATION, uribuilder.build((Object) null).toString());
    }
    IdResponse<?> idResponse = new IdResponse<Object>(createResponse.getId());
    return new AugmentedRestLiResponseData.Builder(routingResult.getResourceMethod().getMethodType()).entity(idResponse)
                                                                                                     .headers(headers)
                                                                                                     .status(createResponse.getStatus())
                                                                                                     .build();
  }
}
