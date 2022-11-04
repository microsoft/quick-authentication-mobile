Pod::Spec.new do |s|
  s.name         = "MicrosoftQuickAuth"
  s.version      = "0.0.5"
  s.summary      = "Enables iOS applications to sign in with Microsoft account."
  s.summary = <<-DESC
  The Microsoft Quick Auth SDK allows users to sign in with their Microsoft account from third-party applications.
   DESC

  s.homepage     = "https://github.com/microsoft/quick-authentication/blob/main/docs/quick-authentication-ios-how-to.md"
  s.license      = {
    :type => "MIT",
    :file => "LICENSE"
  }
  s.authors      = { "Microsoft" => "minggangwang@microsoft.com" }
  s.platform     = :ios
  s.ios.deployment_target = "11.0"
  s.source       = {
    :git => "https://github.com/microsoft/quick-authentication-mobile.git",
    :tag => s.version.to_s,
  }
  s.source_files = "iOS/MSQASignIn/**/*.[mh]"
  s.public_header_files = 'iOS/MSQASignIn/public/*.h'

  s.dependency 'MSAL', '~>1.2'
  s.resource_bundle = {
    'MicrosoftQuickAuth' => ['iOS/MSQASignIn/strings/*',
                             'iOS/MSQASignIn/resources/*']
  }
end
