//------------------------------------------------------------------------------
//
// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
//------------------------------------------------------------------------------

#import "MSQATokenResult_Private.h"

@implementation MSQATokenResult

- (instancetype)initWithAccessToken:(NSString *)accessToken
                authorizationHeader:(NSString *)authorizationHeader
               authenticationScheme:(NSString *)authenticationScheme
                          expiresOn:(NSDate *)expiresOn
                           tenantId:(nullable NSString *)tenantId
                             scopes:(NSArray<NSString *> *)scopes
                      correlationId:(nullable NSUUID *)correlationId {
  self = [super init];
  if (self) {
    _accessToken = accessToken;
    _authorizationHeader = authorizationHeader;
    _authenticationScheme = authenticationScheme;
    _expiresOn = expiresOn;
    _tenantId = tenantId;
    _scopes = scopes;
    _correlationId = correlationId;
  }
  return self;
}

@end
