#import "GameSurfaceView.h"
#import "LauncherPreferences.h"
#import "PLProfiles.h"
#import "utils.h"

@interface GameSurfaceView()
@end

@implementation GameSurfaceView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    self.layer.drawsAsynchronously = YES;
    self.layer.opaque = YES;

    return self;
}

+ (Class)layerClass {
    if ([[PLProfiles resolveKeyForCurrentProfile:@"renderer"] hasPrefix:@"libOSMesa"] || [[PLProfiles resolveKeyForCurrentProfile:@"renderer"] hasPrefix:@"libVirglOSM"]) {
        return CALayer.class;
    } else {
        return CAMetalLayer.class;
    }
}

@end
