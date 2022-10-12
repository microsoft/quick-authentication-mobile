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

#import "TestData.h"

NSString *const kFakeHomeAccountId =
    @"{\"accountIdentifier\" : "
    @"\"00000000-0000-0000-111b-b81e2ee60a77.9188040d-6c67-4c5b-b112-"
    @"36a304b66dad\", \"objectId\":\"00000000-0000-0000-111b-b81e2ee60a77\", "
    @"\"tenantId\":\"9188040d-6c67-4c5b-b112-36a304b66dad\"}";

NSString *const kFakeMSALAccount =
    @"{\"userName\" : \"user@hotmail.com\", \"homeAccountId\" : %@, "
    @"\"environment\":\"login.windows.net\", \"accountClaims\" : {\"name\": "
    @"\"FirstName LastName\"}}";

NSString *const kFakeMSAResult =
    @"{\"accessToken\" : \"access_token\", \"tenantId\": "
    @"\"9188040d-6c67-4c5b-b112-36a304b66dad\", \"idToken\":\"id_token\", "
    @"\"uniqueId\":\"00000000-0000-0000-111b-b81e2ee60a77\"}";

NSString *const kExpectedMSQAAccount =
    @"{\"fullName\":\"FirstName LastName\", \"userName\": "
    @"\"user@hotmail.com\", "
    @"\"userId\":\"111bb81e2ee60a77\",\"idToken\":\"id_token\"}";

NSString *const kNoCachedAccount = @"no-cached-account";
