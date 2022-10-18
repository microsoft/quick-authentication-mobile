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

#import "MSQAAccountInfo+Testing.h"

@implementation MSQAAccountInfo (Testing)

- (NSString *)toJSONString {
  NSDictionary *dict = @{
    @"fullName" : self.fullName,
    @"userName" : self.userName,
    @"userId" : self.userId,
    @"idToken" : self.idToken,
    @"surname" : self.surname,
    @"givenName" : self.givenName,
    @"email" : self.email
  };
  NSData *resultData = [NSJSONSerialization dataWithJSONObject:dict
                                                       options:0
                                                         error:nil];
  return [[NSString alloc] initWithData:resultData
                               encoding:NSUTF8StringEncoding];
}

+ (MSQAAccountInfo *)fromJSONString:(NSString *)str {
  NSData *data = [str dataUsingEncoding:NSUTF8StringEncoding];
  NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data
                                                       options:0
                                                         error:nil];
  MSQAAccountInfo *account =
      [[MSQAAccountInfo alloc] initWithFullName:dict[@"fullName"]
                                       userName:dict[@"userName"]
                                         userId:dict[@"userId"]
                                        idToken:dict[@"idToken"]
                                    accessToken:nil];
  [account setSurname:dict[@"surname"]];
  [account setGivenName:dict[@"givenName"]];
  [account setEmail:dict[@"email"]];
  return account;
}

- (BOOL)isEqual:(MSQAAccountInfo *)account {
  return [self.fullName isEqualToString:account.fullName] &&
         [self.userName isEqualToString:account.userName] &&
         [self.userId isEqualToString:account.userId] &&
         [self.idToken isEqualToString:account.idToken] &&
         [self.surname isEqualToString:account.surname] &&
         [self.givenName isEqualToString:account.givenName] &&
         [self.email isEqualToString:account.email];
}

@end
