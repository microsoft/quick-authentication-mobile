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

#import "MSQALogger_Private.h"

#import <MSAL/MSAL.h>

NS_ASSUME_NONNULL_BEGIN

@implementation MSQALogger {
  MSQALogCallback _callback;
  MSQALogLevel _logLevel;
}

+ (MSQALogger *)sharedInstance {
  static dispatch_once_t once;
  static MSQALogger *sharedInstance;
  dispatch_once(&once, ^{
    sharedInstance = [[self alloc] init];
  });
  return sharedInstance;
}

- (instancetype)init {
  if (!(self = [super init])) {
    return nil;
  }
  _logLevel = MSQALogLevelInfo;
  return self;
}

- (void)setLogCallback:(MSQALogCallback)callback {
  // Because MSAL doesn't allow to set the logger callback multi-times, we
  // follow this pattern here.
  if (_callback != nil) {
    @throw @"MSQA logging callback can only be set once per process and should "
           @"never changed once set.";
  }

  static dispatch_once_t once;
  dispatch_once(&once, ^{
    _callback = callback;
    [MSALGlobalConfig.loggerConfig
        setLogCallback:^(MSALLogLevel level, NSString *_Nullable message,
                         BOOL containsPII) {
          if (self.enableMSALLogging) {
            self->_callback([MSQALogger toMSQALogLevel:level], message);
          }
        }];
  });
}

- (void)logWithLevel:(MSQALogLevel)level format:(NSString *)format, ... {
  if (level > self.logLevel)
    return;

  if (_callback) {
    va_list args;
    va_start(args, format);
    NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
    va_end(args);
    _callback(level, message);
  }
}

- (void)setLogLevel:(MSQALogLevel)logLevel {
  @synchronized(self) {
    MSALGlobalConfig.loggerConfig.logLevel =
        [MSQALogger toMSALLogLevel:logLevel];
    _logLevel = logLevel;
  }
}

- (MSQALogLevel)logLevel {
  return _logLevel;
}

+ (MSALLogLevel)toMSALLogLevel:(MSQALogLevel)level {
  switch (level) {
  case MSQALogLevelNothing:
    return MSALLogLevelNothing;
  case MSQALogLevelError:
    return MSALLogLevelError;
  case MSQALogLevelWarning:
    return MSALLogLevelWarning;
  case MSQALogLevelInfo:
    return MSALLogLevelInfo;
  case MSQALogLevelVerbose:
    return MSALLogLevelVerbose;
  default:
    @throw @"Invalid MSQALogLevel log level";
  }
}

+ (MSQALogLevel)toMSQALogLevel:(MSALLogLevel)level {
  switch (level) {
  case MSALLogLevelNothing:
    return MSQALogLevelNothing;
  case MSALLogLevelError:
    return MSQALogLevelError;
  case MSALLogLevelWarning:
    return MSQALogLevelWarning;
  case MSALLogLevelInfo:
    return MSQALogLevelInfo;
  case MSALLogLevelVerbose:
    return MSQALogLevelVerbose;
  default:
    @throw @"Invalid MSALLogLevel log level";
  }
}

@end

NS_ASSUME_NONNULL_END
