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

#import "MSQATelemetrySender.h"

#import "MSQALogger_Private.h"

static NSString *const kMetricURL = @"https://edge-auth.microsoft.com/metric";

static NSString *const kContentType = @"application/json";

static NSString *const kOrigin = @"https://edge-auth.microsoft.com/";

@interface MSQATelemetrySender ()

@property(nonatomic, readonly, nullable) NSString *uuid;

@property(nonatomic, readonly, nullable) NSString *appVersion;

@property(nonatomic, readonly, nullable) NSString *timestamp;

@end

@implementation MSQATelemetrySender

+ (instancetype)sharedInstance {
  static dispatch_once_t once;
  static MSQATelemetrySender *sharedInstance;
  dispatch_once(&once, ^{
    sharedInstance = [[self alloc] initInternal];
  });
  return sharedInstance;
}

- (instancetype)initInternal {
  if (!(self = [super init])) {
    return nil;
  }
  _uuid = [[NSUUID UUID] UUIDString];
  return self;
}

- (void)sendWithEvent:(NSString *)event message:(NSString *)message {
  NSMutableURLRequest *urlRequest = [NSMutableURLRequest new];
  urlRequest.URL = [NSURL URLWithString:kMetricURL];
  urlRequest.HTTPMethod = @"POST";
  urlRequest.allHTTPHeaderFields =
      @{@"Content-Type" : kContentType, @"Origin" : kOrigin};

  NSData *data =
      [MSQATelemetrySender createTelemetryDataWithEvent:event
                                                   uuid:self.uuid
                                             appVersion:self.appVersion
                                              timestamp:self.timestamp
                                                message:message];
  // Returns directly when testing.
  if (self.callbackForTesting) {
    self.callbackForTesting(data);
    return;
  }

  if (data) {
    [urlRequest setHTTPBody:data];
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *task = [session
        dataTaskWithRequest:urlRequest
          completionHandler:^(NSData *_Nullable data,
                              NSURLResponse *_Nullable response,
                              NSError *_Nullable error) {
            NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
            if (error || httpResponse.statusCode >= 400) {
              [MSQALogger.sharedInstance
                  logWithLevel:MSQALogLevelInfo
                        format:@"Sending telemetry failed."];

              return;
            }
          }];
    [task resume];
  }
}

- (NSString *)timestamp {
  NSDate *date = [NSDate date];
  NSISO8601DateFormatter *formatter = [[NSISO8601DateFormatter alloc] init];
  return [formatter stringFromDate:date];
}

- (NSString *)appVersion {
  return [[[NSBundle mainBundle] infoDictionary]
      objectForKey:@"CFBundleShortVersionString"];
}

+ (NSData *)createTelemetryDataWithEvent:(NSString *)event
                                    uuid:(NSString *)uuid
                              appVersion:(NSString *)appVersion
                               timestamp:(NSString *)timestamp
                                 message:(NSString *)message {
  NSDictionary *eventsDict = [[NSDictionary alloc]
      initWithObjects:@[ event, @1, message, timestamp ]
              forKeys:@[ @"EventName", @"Count", @"Message", @"Timestamp" ]];
  NSArray *events = [NSMutableArray arrayWithObject:eventsDict];
  NSDictionary *payloadDict = [[NSDictionary alloc]
      initWithObjects:@[ uuid, appVersion, events ]
              forKeys:@[ @"EasyAuthSessionId", @"LibVersion", @"events" ]];
  return [NSJSONSerialization dataWithJSONObject:payloadDict
                                         options:NSJSONWritingPrettyPrinted
                                           error:nil];
}

@end
