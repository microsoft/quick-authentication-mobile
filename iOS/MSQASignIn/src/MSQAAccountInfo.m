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

#import "MSQAAccountInfo_Private.h"

NS_ASSUME_NONNULL_BEGIN

@implementation MSQAAccountInfo

- (instancetype)initWithFullName:(NSString *)fullName
                        userName:(NSString *)userName
                          userId:(NSString *)userId
                         idToken:(nullable NSString *)idToken
                     accessToken:(nullable NSString *)accessToken {
  self = [super init];
  if (self) {
    _fullName = fullName;
    _userName = userName;
    _userId = userId;
    _idToken = idToken;
    _accessToken = accessToken;
    _base64Photo = nil;
  }
  return self;
}

- (void)setBase64Photo:(NSString *)base64Photo {
  _base64Photo = base64Photo;
}

- (void)setSurname:(NSString *)surname {
  _surname = surname;
}

- (void)setGivenName:(NSString *)givenName {
  _givenName = givenName;
}

- (void)setEmail:(NSString *)email {
  _email = email;
}

@end

NS_ASSUME_NONNULL_END
