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

@implementation MSQABaseUITest

- (void)setUp {
  [super setUp];
  self.continueAfterFailure = NO;
}

- (void)waitForElement:(id)object {
  NSPredicate *existsPredicate =
      [NSPredicate predicateWithFormat:@"exists == 1"];
  [self expectationForPredicate:existsPredicate
            evaluatedWithObject:object
                        handler:nil];
  [self waitForExpectationsWithTimeout:10.0f handler:nil];
}

- (void)waitForResultInfo:(XCUIElement *)resultInfoTextView
         withExpectedText:(NSString *)text {
  [self waitForElement:resultInfoTextView];
  NSPredicate *predicate =
      [NSPredicate predicateWithFormat:@"value == %@", text];
  [self expectationForPredicate:predicate
            evaluatedWithObject:resultInfoTextView
                        handler:nil];
  [self waitForExpectationsWithTimeout:30.0f handler:nil];
}

- (void)waitForResultStatus:(XCUIElement *)resultStatusTextView {
  [self waitForElement:resultStatusTextView];
  NSPredicate *predicate =
      [NSPredicate predicateWithFormat:@"value == %@", @"done"];
  [self expectationForPredicate:predicate
            evaluatedWithObject:resultStatusTextView
                        handler:nil];
  [self waitForExpectationsWithTimeout:30.0f handler:nil];
}

@end
