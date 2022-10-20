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

#import <Foundation/Foundation.h>
#import <MSQASignIn/MSQATokenResult.h>

NS_ASSUME_NONNULL_BEGIN

@interface MSQATokenResult (Testing)

/// Convert `self` to a JSON string.
- (NSString *)toJSONString;

/// Construct a `MSQATokenResult`from a JSON string.
/// - Parameter str: JSON string
+ (MSQATokenResult *)fromJSONString:(NSString *)str;

/// Compare the `token` with `self`, return `YES` if they are the same.
/// - Parameter token: The token to be compared with.
- (BOOL)isEqual:(MSQATokenResult *)token;

- (instancetype)initWithAccessToken:(NSString *)accessToken
                authorizationHeader:(NSString *)authorizationHeader
               authenticationScheme:(NSString *)authenticationScheme
                          expiresOn:(NSDate *)expiresOn
                           tenantId:(nullable NSString *)tenantId
                             scopes:(NSArray<NSString *> *)scopes
                      correlationId:(nullable NSUUID *)correlationId;

@end

NS_ASSUME_NONNULL_END
