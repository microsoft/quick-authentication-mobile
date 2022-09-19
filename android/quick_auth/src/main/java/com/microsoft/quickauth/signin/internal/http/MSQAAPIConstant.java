//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.internal.http;

public class MSQAAPIConstant {
  public static final int CONNECT_TIMEOUT = 10000;
  public static final int READ_TIMEOUT = 30000;
  public static final String MS_GRAPH_TK_REQUEST_PREFIX = "Bearer ";
  public static final String MS_GRAPH_ROOT_ENDPOINT = "https://graph.microsoft.com/";
  public static final String MS_GRAPH_USER_INFO_PATH = MS_GRAPH_ROOT_ENDPOINT + "v1.0/me";
  public static final String MS_GRAPH_USER_PHOTO_PATH = MS_GRAPH_ROOT_ENDPOINT + "v1.0/me/photo";
  public static final String MS_GRAPH_USER_PHOTO_LARGEST_PATH =
      MS_GRAPH_USER_PHOTO_PATH + "/$value";
  public static final String MS_QUICK_AUTH_ROOT_ENDPOINT = "https://edge-auth.microsoft.com/";
  public static final String MS_QUICK_AUTH_API_PATH = MS_QUICK_AUTH_ROOT_ENDPOINT + "api";
}
