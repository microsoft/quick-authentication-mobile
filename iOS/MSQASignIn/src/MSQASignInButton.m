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

#import "MSQASignInButton.h"

#import <CoreText/CoreText.h>

#import "MSQASignIn_Private.h"

NS_ASSUME_NONNULL_BEGIN

static NSString *const kFontNameSegoeUISemiBold = @"SegoeUI-SemiBold";

static NSString *const kLargeIconImageName = @"Microsoft-Brand-20";
static NSString *const kMediumIconImageName = @"Microsoft-Brand-16";
static NSString *const kSmallIconImageName = @"Microsoft-Brand-12";

static NSString *const kCoderTypeKey = @"type";
static NSString *const kCoderThemeKey = @"theme";
static NSString *const kCoderSizeKey = @"size";
static NSString *const kCoderTextKey = @"text";
static NSString *const kCoderShapeKey = @"shape";
static NSString *const kCoderLogoKey = @"logo";

static const CGFloat kBorderWidth = 1;

static const CGFloat kLargeIconWidth = 20;
static const CGFloat kMediumIconWidth = 16;
static const CGFloat kSmallIconWidth = 12;

static const CGFloat kLargeButtonHeight = 42;
static const CGFloat kMediumButtonHeight = 36;
static const CGFloat kSmallButtonHeight = 28;

static const CGFloat kStandardCornerRadius = 4;

/// The ratio of font size used for accessibility larger text.
static const CGFloat kFontSizeRatioForAccessibilityLarger = 1.25;

static const CGFloat kLargeTextSize = 16;
static const CGFloat kMediumTextSize = 14;
static const CGFloat kSmallTextSize = 12;

static const CGFloat kElementPadding = 12;

static const NSUInteger kNormalBackgroundColorLight = 0xffffffff;
static const NSUInteger kNormalBackgroundColorDark = 0x2f2f2fff;
static const NSUInteger kNormalBorderColorLight = 0xd6d6d6ff;
static const NSUInteger kNormalBorderColorDark = 0x2f2f2fff;

static const NSUInteger kPressedBackgroundColorLight = 0xefefefff;
static const NSUInteger kPressedBackgroundColorDark = 0x272727ff;
static const NSUInteger kPressedBorderColorLight = 0xd6d6d6ff;
static const NSUInteger kPressedBorderColorDark = 0x272727ff;

static const NSUInteger kTextColorLight = 0x323130ff;
static const NSUInteger kTextColorDark = 0xffffffff;

static UIColor *UIColorFromHexValue(NSUInteger hex) {
  CGFloat red = ((hex & 0xff000000) >> 24) / 255.0f;
  CGFloat green = ((hex & 0x00ff0000) >> 16) / 255.0f;
  CGFloat blue = ((hex & 0x0000ff00) >> 8) / 255.0f;
  CGFloat alpha = ((hex & 0x000000ff) >> 0) / 255.0f;
  return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
}

typedef NS_ENUM(NSUInteger, MSQASignInButtonState) {
  kMSQASignInButtonStateNormal = 0,
  kMSQASignInButtonStateDisabled = 1,
  kMSQASignInButtonStatePressed = 2,
};

@interface MSQASignInButton ()

@property(nonatomic, assign) MSQASignInButtonState buttonState;

@property(nonatomic, strong) UIImageView *iconView;

/// Indicate if the layout direction is from right to left.
@property(nonatomic, assign) BOOL isRTL;

/// Indicate if the accessibility large text is enabled.
@property(nonatomic, assign) BOOL isAccessibilityLargerEnabled;

@end

@implementation MSQASignInButton {
  MSQASignInClient *_msSignInClient;
  UIViewController *_viewController;
  MSQACompletionBlock _completionBlock;
}

- (instancetype)initWithFrame:(CGRect)frame {
  self = [super initWithFrame:frame];
  if (self) {
    [self initInternal];
  }
  return self;
}

- (nullable instancetype)initWithCoder:(NSCoder *)coder {
  self = [super initWithCoder:coder];
  if (self) {
    [self initInternal];
    if ([coder containsValueForKey:kCoderTypeKey]) {
      _type = [coder decodeIntegerForKey:kCoderTypeKey];
    }
    if ([coder containsValueForKey:kCoderThemeKey]) {
      _theme = [coder decodeIntegerForKey:kCoderThemeKey];
    }
    if ([coder containsValueForKey:kCoderSizeKey]) {
      _size = [coder decodeIntegerForKey:kCoderSizeKey];
    }
    if ([coder containsValueForKey:kCoderTextKey]) {
      _text = [coder decodeIntegerForKey:kCoderTextKey];
    }
    if ([coder containsValueForKey:kCoderShapeKey]) {
      _shape = [coder decodeIntegerForKey:kCoderShapeKey];
    }
    if ([coder containsValueForKey:kCoderLogoKey]) {
      _logo = [coder decodeIntegerForKey:kCoderLogoKey];
    }
  }
  return self;
}

- (void)initInternal {
  _type = kMSQASignInButtonTypeStandard;
  _theme = kMSQASignInButtonThemeLight;
  _size = kMSQASignInButtonSizeLarge;
  _text = kMSQASignInButtonTextSignInWith;
  _shape = kMSQASignInButtonShapeRectangular;
  _logo = kMSQASignInButtonLogoLeft;
  _buttonState = kMSQASignInButtonStateNormal;
  self.isAccessibilityElement = YES;
  self.accessibilityTraits = UIAccessibilityTraitButton;
  self.accessibilityLabel = [self buttonTextString];

  [self addTarget:self
                action:@selector(buttonDidTouch)
      forControlEvents:UIControlEventAllTouchEvents];
  [self addTarget:self
                action:@selector(buttonDidPress)
      forControlEvents:UIControlEventTouchDown | UIControlEventTouchDragEnter |
                       UIControlEventTouchDragInside];
  [self addTarget:self
                action:@selector(buttonDidRelease)
      forControlEvents:UIControlEventTouchCancel | UIControlEventTouchDragExit |
                       UIControlEventTouchDragOutside |
                       UIControlEventTouchUpInside];

  [self addTarget:self
                action:@selector(onButtonClicked)
      forControlEvents:UIControlEventTouchUpInside];

  [[NSNotificationCenter defaultCenter]
      addObserver:self
         selector:@selector(didChangePreferredContentSize:)
             name:UIContentSizeCategoryDidChangeNotification
           object:nil];
  self.isAccessibilityLargerEnabled = [self isAccessibilityLarger];

  self.clipsToBounds = YES;
  self.backgroundColor = [UIColor clearColor];

  _iconView = [[UIImageView alloc] init];
  _iconView.contentMode = UIViewContentModeCenter;
  _iconView.userInteractionEnabled = NO;
  [self addSubview:_iconView];
  [self updateButtonIconImage];

  [self updateUI];
}

- (void)drawButtonBackground:(CGContextRef)context {
  CGContextSaveGState(context);

  CGContextScaleCTM(context, 1, -1);
  CGContextTranslateCTM(context, 0, -self.bounds.size.height);

  CGMutablePathRef path = CGPathCreateMutable();
  CGFloat radius = [self buttonRectRadius];
  CGPathAddRoundedRect(
      path, NULL, CGRectInset(self.bounds, kBorderWidth / 2, kBorderWidth / 2),
      radius, radius);

  CGContextAddPath(context, path);
  CGContextSetFillColorWithColor(context, [self buttonBackgroundColor].CGColor);
  CGContextSetStrokeColorWithColor(context, [self buttonBorderColor].CGColor);
  CGContextSetLineWidth(context, kBorderWidth);
  CGContextDrawPath(context, kCGPathFillStroke);

  CGPathRelease(path);

  CGContextRestoreGState(context);
}

- (void)drawButtonText {
  if (_type == kMSQASignInButtonTypeIcon) {
    return;
  }
  NSString *string = [self buttonTextString];
  UIFont *font = [self buttonTextFont];
  CGSize size = [self buttonTextSize];
  UIColor *color = [self buttonTextColor];

  CGFloat x = 0;
  switch (_logo) {
  case kMSQASignInButtonLogoLeft:
    x = kElementPadding * 2 + [self buttonIconWidth];
    break;
  case kMSQASignInButtonLogoLeftTextCenter:
  case kMSQASignInButtonLogoCenter:
    x = round((self.bounds.size.width + [self buttonIconWidth] +
               kElementPadding - size.width) /
              2);
    break;
  }

  if (self.isRTL) {
    x = self.bounds.size.width - x - size.width;
  }
  CGFloat y = round((self.bounds.size.height - size.height) / 2);

  [string drawAtPoint:CGPointMake(x, y)
       withAttributes:@{
         NSFontAttributeName : font,
         NSForegroundColorAttributeName : color
       }];
}

- (void)updateButtonIconFrame {
  [_iconView setFrame:[self buttonIconFrame]];
}

- (void)updateButtonIconImage {
  NSString *imagePath = [self buttonIconImagePath];
  if (!imagePath) {
    _iconView.image = nil;
    return;
  }
  UIImage *image = [UIImage imageWithContentsOfFile:imagePath];
  _iconView.image = image;
}

- (BOOL)setSignInClient:(MSQASignInClient *)msSignInClient
         viewController:(UIViewController *)viewController
        completionBlock:(MSQACompletionBlock)completionBlock {
  _msSignInClient = msSignInClient;
  _viewController = viewController;
  _completionBlock = completionBlock;
  return msSignInClient && viewController && completionBlock;
}

- (BOOL)isRTL {
  return self.effectiveUserInterfaceLayoutDirection ==
         UIUserInterfaceLayoutDirectionRightToLeft;
}

#pragma mark - Override

- (void)setFrame:(CGRect)frame {
  frame.size = [self sizeThatFits:frame.size];
  if (CGRectEqualToRect(frame, self.frame)) {
    return;
  }
  [super setFrame:frame];
  [self setNeedsUpdateConstraints];
  [self setNeedsDisplay];
}

- (CGSize)sizeThatFits:(CGSize)size {
  if (_type == kMSQASignInButtonTypeIcon) {
    switch (_size) {
    case kMSQASignInButtonSizeLarge:
      return CGSizeMake(kLargeButtonHeight, kLargeButtonHeight);
    case kMSQASignInButtonSizeMedium:
      return CGSizeMake(kMediumButtonHeight, kMediumButtonHeight);
    case kMSQASignInButtonSizeSmall:
      return CGSizeMake(kSmallButtonHeight, kSmallButtonHeight);
    }
  }
  CGFloat minWidth = ceil([self buttonTextSize].width + [self buttonIconWidth] +
                          kElementPadding * 3);
  CGFloat width = MAX(size.width, minWidth);
  switch (_size) {
  case kMSQASignInButtonSizeLarge:
    return CGSizeMake(width, kLargeButtonHeight);
  case kMSQASignInButtonSizeMedium:
    return CGSizeMake(width, kMediumButtonHeight);
  case kMSQASignInButtonSizeSmall:
    return CGSizeMake(width, kSmallButtonHeight);
  }
}

- (void)drawRect:(CGRect)rect {
  [super drawRect:rect];

  CGContextRef context = UIGraphicsGetCurrentContext();
  if (!context) {
    return;
  }

  CGContextRetain(context);

  [self drawButtonBackground:context];
  [self drawButtonText];
  [self updateButtonIconFrame];

  CGContextRelease(context);
}

- (void)updateConstraints {
  CGSize sizeThatFits = [self sizeThatFits:CGSizeZero];

  NSLayoutConstraint *heightConstraint =
      [NSLayoutConstraint constraintWithItem:self
                                   attribute:NSLayoutAttributeHeight
                                   relatedBy:NSLayoutRelationEqual
                                      toItem:nil
                                   attribute:NSLayoutAttributeNotAnAttribute
                                  multiplier:1
                                    constant:sizeThatFits.height];
  heightConstraint.identifier =
      @"buttonHeight - auto calculated by MSQASignInButton";

  NSLayoutRelation widthConstraintRelation =
      _type == kMSQASignInButtonTypeIcon ? NSLayoutRelationEqual
                                         : NSLayoutRelationGreaterThanOrEqual;
  NSLayoutConstraint *widthConstraint =
      [NSLayoutConstraint constraintWithItem:self
                                   attribute:NSLayoutAttributeWidth
                                   relatedBy:widthConstraintRelation
                                      toItem:nil
                                   attribute:NSLayoutAttributeNotAnAttribute
                                  multiplier:1
                                    constant:sizeThatFits.width];
  widthConstraint.identifier =
      @"buttonWidth - auto calculated by MSQASignInButton";

  BOOL needAddHeightConstraint = YES, needAddWidthConstraint = YES;
  for (NSLayoutConstraint *constraint in self.constraints) {
    if (constraint.firstItem != self) {
      continue;
    }
    if ([self isConstraint:constraint equalToConstraint:heightConstraint]) {
      needAddHeightConstraint = NO;
      continue;
    }
    if ([self isConstraint:constraint equalToConstraint:widthConstraint]) {
      needAddWidthConstraint = NO;
      continue;
    }
    if (constraint.firstAttribute == NSLayoutAttributeHeight) {
      [self removeConstraint:constraint];
      continue;
    }
    if (constraint.firstAttribute == NSLayoutAttributeWidth) {
      if (_type == kMSQASignInButtonTypeIcon) {
        [self removeConstraint:constraint];
        continue;
      }
      if (constraint.constant < sizeThatFits.width) {
        [self removeConstraint:constraint];
        continue;
      }
    }
  }
  if (needAddHeightConstraint) {
    [self addConstraint:heightConstraint];
  }
  if (needAddWidthConstraint) {
    [self addConstraint:widthConstraint];
  }
  [super updateConstraints];
}

#pragma mark - Private

- (void)updateUI {
  [self setFrame:self.frame];
  [self setNeedsUpdateConstraints];
  [self setNeedsDisplay];
}

- (BOOL)isConstraint:(NSLayoutConstraint *)constraintA
    equalToConstraint:(NSLayoutConstraint *)constraintB {
  return constraintA.priority == constraintB.priority &&
         constraintA.firstItem == constraintB.firstItem &&
         constraintA.firstAttribute == constraintB.firstAttribute &&
         constraintA.relation == constraintB.relation &&
         constraintA.secondItem == constraintB.secondItem &&
         constraintA.secondAttribute == constraintB.secondAttribute &&
         constraintA.multiplier == constraintB.multiplier &&
         constraintA.constant == constraintB.constant;
}

- (BOOL)isAccessibilityLarger {
  NSArray *largeSizes = @[
    UIContentSizeCategoryAccessibilityLarge,
    UIContentSizeCategoryAccessibilityExtraLarge,
    UIContentSizeCategoryAccessibilityExtraExtraLarge,
    UIContentSizeCategoryAccessibilityExtraExtraExtraLarge
  ];
  return [largeSizes containsObject:[[UIApplication sharedApplication]
                                        preferredContentSizeCategory]];
}

- (void)didChangePreferredContentSize:(NSNotification *)notification {
  BOOL isAccessibilityLarger = [self isAccessibilityLarger];
  if (self.isAccessibilityLargerEnabled != isAccessibilityLarger) {
    self.isAccessibilityLargerEnabled = isAccessibilityLarger;
    [self updateUI];
  }
}

#pragma mark - Handle user interaction

- (void)buttonDidTouch {
  [self setNeedsDisplay];
}

- (void)buttonDidPress {
  self.buttonState = kMSQASignInButtonStatePressed;
}

- (void)buttonDidRelease {
  self.buttonState = kMSQASignInButtonStateNormal;
}

- (void)onButtonClicked {
  // Performing selector `willSignIn:` is needed by the automation test where we
  // will set the MSAL status before starting to sign in.
  if (_viewController &&
      [_viewController respondsToSelector:@selector(willSignIn:)]) {
    [_viewController performSelector:@selector(willSignIn:) withObject:self];
  }

  if (_msSignInClient) {
    [_msSignInClient signInByButtonWithViewController:_viewController
                                      completionBlock:_completionBlock];
  }
}

#pragma mark - Setter

- (void)setButtonState:(MSQASignInButtonState)buttonState {
  if (_buttonState == buttonState) {
    return;
  }
  _buttonState = buttonState;
  [self setNeedsDisplay];
}

- (void)setType:(MSQASignInButtonType)type {
  if (_type == type) {
    return;
  }
  _type = type;
  [self updateUI];
}

- (void)setTheme:(MSQASignInButtonTheme)theme {
  if (_theme == theme) {
    return;
  }
  _theme = theme;
  [self updateUI];
}

- (void)setSize:(MSQASignInButtonSize)size {
  if (_size == size) {
    return;
  }
  _size = size;
  [self updateButtonIconImage];
  [self updateUI];
}

- (void)setText:(MSQASignInButtonText)text {
  if (_text == text) {
    return;
  }
  _text = text;
  self.accessibilityLabel = [self buttonTextString];
  [self updateUI];
}

- (void)setShape:(MSQASignInButtonShape)shape {
  if (_shape == shape) {
    return;
  }
  _shape = shape;
  [self updateUI];
}

- (void)setLogo:(MSQASignInButtonLogo)logo {
  if (_logo == logo) {
    return;
  }
  _logo = logo;
  [self updateUI];
}

#pragma mark - Getter

- (CGFloat)buttonRectRadius {
  switch (_shape) {
  case kMSQASignInButtonShapeRectangular:
    return 0;
  case kMSQASignInButtonShapeRounded:
    return kStandardCornerRadius;
  case kMSQASignInButtonShapePill:
    return (self.bounds.size.height - kBorderWidth) / 2;
  }
}

- (UIColor *)buttonBackgroundColor {
  switch (_buttonState) {
  case kMSQASignInButtonStateNormal:
  case kMSQASignInButtonStateDisabled:
    return UIColorFromHexValue(_theme == kMSQASignInButtonThemeLight
                                   ? kNormalBackgroundColorLight
                                   : kNormalBackgroundColorDark);
    break;
  case kMSQASignInButtonStatePressed:
    return UIColorFromHexValue(_theme == kMSQASignInButtonThemeLight
                                   ? kPressedBackgroundColorLight
                                   : kPressedBackgroundColorDark);
    break;
  }
}

- (UIColor *)buttonBorderColor {
  switch (_buttonState) {
  case kMSQASignInButtonStateNormal:
  case kMSQASignInButtonStateDisabled:
    return UIColorFromHexValue(_theme == kMSQASignInButtonThemeLight
                                   ? kNormalBorderColorLight
                                   : kNormalBorderColorDark);
  case kMSQASignInButtonStatePressed:
    return UIColorFromHexValue(_theme == kMSQASignInButtonThemeLight
                                   ? kPressedBorderColorLight
                                   : kPressedBorderColorDark);
  }
}

- (CGFloat)buttonIconWidth {
  switch (_size) {
  case kMSQASignInButtonSizeLarge:
    return kLargeIconWidth;
  case kMSQASignInButtonSizeMedium:
    return kMediumIconWidth;
  case kMSQASignInButtonSizeSmall:
    return kSmallIconWidth;
  }
}

- (NSString *)buttonIconImagePath {
  switch (_size) {
  case kMSQASignInButtonSizeLarge:
    return [[MSQASignInButton getFrameworkBundle]
        pathForResource:kLargeIconImageName
                 ofType:@"png"];
  case kMSQASignInButtonSizeMedium:
    return [[MSQASignInButton getFrameworkBundle]
        pathForResource:kMediumIconImageName
                 ofType:@"png"];
  case kMSQASignInButtonSizeSmall:
    return [[MSQASignInButton getFrameworkBundle]
        pathForResource:kSmallIconImageName
                 ofType:@"png"];
  }
}

- (CGRect)buttonIconFrame {
  if (_type == kMSQASignInButtonTypeIcon) {
    CGFloat iconWidth = [self buttonIconWidth];
    CGFloat x = (self.bounds.size.width - iconWidth) / 2;
    CGFloat y = (self.bounds.size.height - iconWidth) / 2;
    return CGRectMake(x, y, iconWidth, iconWidth);
  }
  CGFloat iconWidth = [self buttonIconWidth];
  CGFloat textWidth = [self buttonTextSize].width;
  CGFloat x = 0;
  switch (_logo) {
  case kMSQASignInButtonLogoLeft:
  case kMSQASignInButtonLogoLeftTextCenter:
    x = kElementPadding;
    break;
  case kMSQASignInButtonLogoCenter:
    x = (self.bounds.size.width - iconWidth - kElementPadding - textWidth) / 2;
    break;
  }

  if (self.isRTL) {
    x = self.bounds.size.width - x - [self buttonIconWidth];
  }
  CGFloat y = (self.bounds.size.height - iconWidth) / 2;
  return CGRectMake(x, y, iconWidth, iconWidth);
}

+ (nullable NSBundle *)getFrameworkBundle {
  NSString *const kBundlePath = @"MicrosoftQuickAuth";
  NSString *path = [[NSBundle mainBundle] pathForResource:kBundlePath
                                                   ofType:@"bundle"];
  if (!path) {
    path = [[NSBundle bundleForClass:[MSQASignInButton class]]
        pathForResource:kBundlePath
                 ofType:@"bundle"];
  }
  return [NSBundle bundleWithPath:path];
}

- (NSString *)buttonTextString {
  NSString *const kStringsTableName = @"Localizable";

  switch (_text) {
  case kMSQASignInButtonTextSignInWith:
    return [[MSQASignInButton getFrameworkBundle]
        localizedStringForKey:@"msqa_signin_with_text"
                        value:nil
                        table:kStringsTableName];
  case kMSQASignInButtonTextSignUpWith:
    return [[MSQASignInButton getFrameworkBundle]
        localizedStringForKey:@"msqa_signup_with_text"
                        value:nil
                        table:kStringsTableName];
  case kMSQASignInButtonTextContinueWith:
    return [[MSQASignInButton getFrameworkBundle]
        localizedStringForKey:@"msqa_continue_with_text"
                        value:nil
                        table:kStringsTableName];
  case kMSQASignInButtonTextSignIn:
    return [[MSQASignInButton getFrameworkBundle]
        localizedStringForKey:@"msqa_signin_text"
                        value:nil
                        table:kStringsTableName];
  }
}

- (UIColor *)buttonTextColor {
  return UIColorFromHexValue(
      _theme == kMSQASignInButtonThemeLight ? kTextColorLight : kTextColorDark);
}

- (UIFont *)buttonTextFont {
  CGFloat size =
      self.isAccessibilityLargerEnabled
          ? kFontSizeRatioForAccessibilityLarger * [self buttonTextFontSize]
          : [self buttonTextFontSize];
  // We only try to apply the SegoeUI-SemiBold font for English, other languages
  // use the system default.
  if (![MSQASignInButton isEnglish]) {
    return [UIFont systemFontOfSize:size];
  }

  UIFont *font = [UIFont fontWithName:kFontNameSegoeUISemiBold size:size];
  if (!font) {
    NSString *path = [[NSBundle bundleForClass:[self class]]
        pathForResource:kFontNameSegoeUISemiBold
                 ofType:@"ttf"];
    if (!path) {
      return [UIFont boldSystemFontOfSize:size];
    }
    CGDataProviderRef provider =
        CGDataProviderCreateWithFilename([path UTF8String]);
    CFErrorRef error;
    CGFontRef loadedFont = CGFontCreateWithDataProvider(provider);
    if (!loadedFont || !CTFontManagerRegisterGraphicsFont(loadedFont, &error)) {
      CGDataProviderRelease(provider);
      return [UIFont boldSystemFontOfSize:size];
    }
    CGFontRelease(loadedFont);
    CGDataProviderRelease(provider);
    font = [UIFont fontWithName:kFontNameSegoeUISemiBold size:size];
    return font ? font : [UIFont boldSystemFontOfSize:size];
  }
  return font;
}

- (CGFloat)buttonTextFontSize {
  switch (_size) {
  case kMSQASignInButtonSizeLarge:
    return kLargeTextSize;
  case kMSQASignInButtonSizeMedium:
    return kMediumTextSize;
  case kMSQASignInButtonSizeSmall:
    return kSmallTextSize;
  }
}

- (CGSize)buttonTextSize {
  return [[self buttonTextString]
             boundingRectWithSize:CGSizeMake(CGFLOAT_MAX, CGFLOAT_MAX)
                          options:0
                       attributes:@{NSFontAttributeName : [self buttonTextFont]}
                          context:nil]
      .size;
}

#pragma mark - Class methods

+ (BOOL)isEnglish {
  NSString *language =
      [[[NSBundle mainBundle] preferredLocalizations] objectAtIndex:0];
  return [language isEqualToString:@"en"];
}

@end

NS_ASSUME_NONNULL_END
