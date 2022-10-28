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

#import <XCTest/XCTest.h>

#import "MSQAAccountInfo_Private.h"

static NSString *const kFullName = @"fullName";
static NSString *const kUserName = @"userName";
static NSString *const kUserId = @"userId";
static NSString *const kIdToken = @"idToken";
static NSString *const kAccessToken = @"accessToken";
static NSString *const kBase64Photo = @"base64Photo";
static NSString *const kSurname = @"surname";
static NSString *const kGivenName = @"givenName";
static NSString *const kEmail = @"email";

@interface MSQAAccountInfoTests : XCTestCase

@end

@implementation MSQAAccountInfoTests

- (void)testMSQAAccountInfo_initialization {
  // Test initialization.
  MSQAAccountInfo *account =
      [[MSQAAccountInfo alloc] initWithFullName:kFullName
                                       userName:kUserName
                                         userId:kUserId
                                        idToken:kIdToken
                                    accessToken:kAccessToken];
  XCTAssertTrue([account.fullName isEqualToString:kFullName]);
  XCTAssertTrue([account.userName isEqualToString:kUserName]);
  XCTAssertTrue([account.userId isEqualToString:kUserId]);
  XCTAssertTrue([account.idToken isEqualToString:kIdToken]);
  XCTAssertTrue([account.accessToken isEqualToString:kAccessToken]);

  // Test property setter.
  account.base64Photo = kBase64Photo;
  account.surname = kSurname;
  account.givenName = kGivenName;
  account.email = kEmail;
  XCTAssertTrue([account.base64Photo isEqualToString:kBase64Photo]);
  XCTAssertTrue([account.surname isEqualToString:kSurname]);
  XCTAssertTrue([account.givenName isEqualToString:kGivenName]);
  XCTAssertTrue([account.email isEqualToString:kEmail]);
}

@end
