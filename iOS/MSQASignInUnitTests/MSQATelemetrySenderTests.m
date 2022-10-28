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

#import "MSQATelemetrySender.h"

static NSString *const kTestEvent = @"Test.Event";
static NSString *const kSuccessMessage = @"success";

@interface MSQATelemetrySenderTests : XCTestCase

@end

@implementation MSQATelemetrySenderTests

- (void)testMSQATelemetrySender_withSendEvent_message {
  MSQATelemetrySender *sender = MSQATelemetrySender.sharedInstance;
  XCTAssertNotNil(sender);

  MSQATelemetrySender.sharedInstance.callbackForTesting = ^(NSData *jsonData) {
    XCTAssertNotNil(jsonData);

    NSError *error = nil;
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData
                                                         options:nil
                                                           error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(dict[@"EasyAuthSessionId"]);
    XCTAssertNotNil(dict[@"LibVersion"]);

    NSArray *events = dict[@"events"];
    XCTAssertNotNil(events);
    XCTAssertEqual(events.count, 1u);
    NSDictionary *event = [events objectAtIndex:0u];
    XCTAssertTrue([event[@"EventName"] isEqualToString:kTestEvent]);
    XCTAssertTrue([event[@"Message"] isEqualToString:kSuccessMessage]);
    XCTAssertEqual([event[@"Count"] integerValue], 1);
    XCTAssertNotNil(event[@"Timestamp"]);
  };

  [MSQATelemetrySender.sharedInstance sendWithEvent:kTestEvent
                                            message:kSuccessMessage];
}

@end
