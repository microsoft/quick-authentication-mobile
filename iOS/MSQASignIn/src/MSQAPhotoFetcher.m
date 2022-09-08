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

#import "MSQAPhotoFetcher.h"

NS_ASSUME_NONNULL_BEGIN

@implementation MSQAPhotoFetcher {
  NSString *_token;
}

+ (instancetype)fetchPhotoWithToken:(NSString *)token
                    completionBlock:
                        (void (^)(NSString *_Nullable photo,
                                  NSError *_Nullable error))completionBlock {
  MSQAPhotoFetcher *fetcher = [MSQAPhotoFetcher new];
  fetcher->_token = token;
  [fetcher startInternalWithCompletionBlock:completionBlock];

  return fetcher;
}

#pragma mark - Private Methods

+ (NSURL *)graphURLWithPath:(NSString *)path {
  NSString *urlString =
      [NSString stringWithFormat:@"https://graph.microsoft.com/v1.0/%@", path];
  return [NSURL URLWithString:urlString];
}

- (void)startInternalWithCompletionBlock:
    (void (^)(NSString *_Nullable photo,
              NSError *_Nullable error))completionBlock {
  [self getJSON:@"me/photo"
      completionBlock:^(NSDictionary *json, NSError *error) {
        if (error) {
          completionBlock(nil, error);
          return;
        }

        [self getData:@"me/photo/$value"
            completionBlock:^(NSData *data, NSError *error) {
              if (error) {
                completionBlock(nil, error);
                return;
              }
              completionBlock(
                  [data base64EncodedStringWithOptions:
                            NSDataBase64EncodingEndLineWithLineFeed],
                  nil);
            }];
      }];
}

- (void)getJSON:(NSString *)path
    completionBlock:(void (^)(NSDictionary *_Nullable json,
                              NSError *error))completionBlock {
  [self getData:path
      completionBlock:^(NSData *data, NSError *error) {
        if (error) {
          completionBlock(nil, error);
          return;
        }

        NSError *localError = nil;
        NSDictionary *json =
            [NSJSONSerialization JSONObjectWithData:data
                                            options:0
                                              error:&localError];
        completionBlock(json, localError);
      }];
}

- (void)getData:(NSString *)path
    completionBlock:(void (^)(NSData *_Nullable data,
                              NSError *_Nullable error))completionBlock {
  NSMutableURLRequest *urlRequest = [NSMutableURLRequest new];
  urlRequest.URL = [MSQAPhotoFetcher graphURLWithPath:path];
  urlRequest.HTTPMethod = @"GET";
  urlRequest.allHTTPHeaderFields =
      @{@"Authorization" : [NSString stringWithFormat:@"Bearer %@", _token]};

  NSURLSession *session = [NSURLSession sharedSession];

  NSURLSessionDataTask *task = [session
      dataTaskWithRequest:urlRequest
        completionHandler:^(NSData *_Nullable data,
                            NSURLResponse *_Nullable response,
                            NSError *_Nullable error) {
          if (error) {
            completionBlock(nil, error);
            return;
          }

          NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
          if (httpResponse.statusCode != 200) {
            NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data
                                                                 options:0
                                                                   error:nil];
            completionBlock(nil,
                            [NSError errorWithDomain:@"GraphErrorDomain"
                                                code:httpResponse.statusCode
                                            userInfo:json[@"error"]]);
            return;
          }
          completionBlock(data, nil);
        }];
  [task resume];
}

@end

NS_ASSUME_NONNULL_END
