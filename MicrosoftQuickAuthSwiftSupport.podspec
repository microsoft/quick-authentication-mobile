Pod::Spec.new do |s|
  s.name                  = 'MicrosoftQuickAuthSwiftSupport'
  s.version               = '0.0.1'
  s.summary               = 'Add SwiftUI support for MicrosoftQuickAuth.'
  s.homepage              = 'https://github.com/microsoft/quick-authentication/blob/main/docs/quick-authentication-ios-how-to.md'
  s.license               = { :type => 'MIT', :file => 'LICENSE' }
  s.authors               = { 'Microsoft' => 'minggangwang@microsoft.com' 
                              'Microsoft' => 'yueqizhao@microsoft.com' }
  s.platform              = :ios
  s.ios.deployment_target = '13.0'
  s.source                = { :git => 'https://github.com/microsoft/quick-authentication-mobile.git', :tag => s.version.to_s }
  s.module_name           = 'MSQASignInSwift'
  s.prefix_header_file    = false
  s.source_files          = 'iOS/MSQASignInSwift/*.swift'
  s.frameworks            = 'SwiftUI'
  s.dependency 'MicrosoftQuickAuth', '~> 0.0.5'
end