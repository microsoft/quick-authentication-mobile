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

#import "MSQABaseUITest.h"

#import "MSQAAccountInfo+Testing.h"
#import "MSQATokenResult+Testing.h"
#import "TestData.h"

@interface AcquireTokenInteractiveTest : MSQABaseUITest

@end

@implementation AcquireTokenInteractiveTest

- (void)testAcquireTokenInteractivelyTest_withAccept {
  XCUIApplication *app = [[XCUIApplication alloc] init];
  [app launch];
  XCUIElement *button = app.buttons[@"fetch token interactive"];
  [self waitForElement:button];
  [button tap];

  XCUIElement *resultStatus = app.textViews[@"Result Status"];
  [self waitForResultStatus:resultStatus];

  XCUIElement *resultInfo = app.textViews[@"Result Info"];
  MSQATokenResult *expected = [MSQATokenResult fromJSONString:kFakeMSALResult];
  MSQATokenResult *actual = [MSQATokenResult fromJSONString:resultInfo.value];
  XCTAssertTrue([actual isEqual:expected]);
}

- (void)testAcquireTokenInteractivelyTest_withCancel {
  XCUIApplication *app = [[XCUIApplication alloc] init];
  [app launch];
  XCUIElement *button = app.buttons[@"fetch token interactive with cancel"];
  [self waitForElement:button];
  [button tap];

  XCUIElement *resultStatus = app.textViews[@"Result Status"];
  [self waitForResultStatus:resultStatus];

  XCUIElement *resultInfo = app.textViews[@"Result Info"];
  XCTAssertTrue([resultInfo.value isEqual:@"access_denied"]);
}

@end
