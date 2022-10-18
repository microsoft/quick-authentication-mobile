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

#import "MSQATokenResult+Testing.h"

@implementation MSQATokenResult (Testing)

- (NSString *)toJSONString {
  NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
  [formatter setDateFormat:@"yyyy-MM-dd"];
  NSDictionary *dict = @{
    @"accessToken" : self.accessToken,
    @"authorizationHeader" : self.authorizationHeader,
    @"authenticationScheme" : self.authenticationScheme,
    @"expiresOn" : [formatter stringFromDate:self.expiresOn],
    @"tenantId" : self.tenantId,
    @"scopes" : self.scopes,
    @"correlationId" : [self.correlationId UUIDString]
  };

  NSData *data = [NSJSONSerialization dataWithJSONObject:dict
                                                 options:0
                                                   error:nil];
  return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}

+ (MSQATokenResult *)fromJSONString:(NSString *)str {
  NSData *data = [str dataUsingEncoding:NSUTF8StringEncoding];
  NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data
                                                       options:0
                                                         error:nil];

  NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
  [formatter setDateFormat:@"yyyy-MM-dd"];
  NSDate *expiresOn = [formatter dateFromString:dict[@"expiresOn"]];
  NSUUID *uuid = [[NSUUID alloc] initWithUUIDString:dict[@"correlationId"]];
  return
      [[MSQATokenResult alloc] initWithAccessToken:dict[@"accessToken"]
                               authorizationHeader:@"authorizationHeader"
                              authenticationScheme:dict[@"authenticationScheme"]
                                         expiresOn:expiresOn
                                          tenantId:dict[@"tenantId"]
                                            scopes:dict[@"scopes"]
                                     correlationId:uuid];
}

- (BOOL)isEqual:(MSQATokenResult *)token {
  return
      [self.accessToken isEqualToString:token.accessToken] &&
      [self.authorizationHeader isEqualToString:token.authorizationHeader] &&
      [self.authenticationScheme isEqualToString:token.authenticationScheme] &&
      ([self.expiresOn compare:token.expiresOn] == NSOrderedSame) &&
      [self.tenantId isEqualToString:token.tenantId] &&
      [self.scopes isEqualToArray:token.scopes] &&
      [self.correlationId isEqual:token.correlationId];
}

@end
