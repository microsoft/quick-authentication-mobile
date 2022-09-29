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

NS_ASSUME_NONNULL_BEGIN

/// Used to send the metrics back to the server, and the telemetry format is a
/// JSON object, which is defined as:
/// {
///  "events":[
///   {
///      "EventName":"getCurrentAccount",
///      "Message":"no-account-present",
///      "Count":1,
///      "Timestamp":"2022-09-26T08:26:01Z"
///   }
///  ],
///  "EasyAuthSessionId":"C5D2FCD6-1F9D-41D7-AB3F-A2DB394F0AA5",
///  "LibVersion":"1.0"
/// }
/// Accessing the signleton instance through`sharedInstance` property.
@interface MSQATelemetrySender : NSObject

@property(class, nonatomic, readonly) MSQATelemetrySender *sharedInstance;

+ (instancetype)new NS_UNAVAILABLE;

+ (instancetype)init NS_UNAVAILABLE;

- (void)sendWithEvent:(NSString *)event message:(NSString *)message;

@end

NS_ASSUME_NONNULL_END
