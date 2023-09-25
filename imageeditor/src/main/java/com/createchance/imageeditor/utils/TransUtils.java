package com.createchance.imageeditor.utils;

import com.createchance.imageeditor.transitions.AbstractTransition;
import com.createchance.imageeditor.transitions.AngularTransition;
import com.createchance.imageeditor.transitions.BounceTransition;
import com.createchance.imageeditor.transitions.BowTieHorizontalTransition;
import com.createchance.imageeditor.transitions.BowTieVerticalTransition;
import com.createchance.imageeditor.transitions.BurnTransition;
import com.createchance.imageeditor.transitions.ButterflyWaveScrawlerTransition;
import com.createchance.imageeditor.transitions.CannabisLeafTransition;
import com.createchance.imageeditor.transitions.CircleCropTransition;
import com.createchance.imageeditor.transitions.CircleOpenTransition;
import com.createchance.imageeditor.transitions.CircleTransition;
import com.createchance.imageeditor.transitions.ColorDistanceTransition;
import com.createchance.imageeditor.transitions.ColorPhaseTransition;
import com.createchance.imageeditor.transitions.CrazyParametricFunTransition;
import com.createchance.imageeditor.transitions.CrossHatchTransition;
import com.createchance.imageeditor.transitions.CrossWarpTransition;
import com.createchance.imageeditor.transitions.CrossZoomTransition;
import com.createchance.imageeditor.transitions.CubeTransition;
import com.createchance.imageeditor.transitions.DirectionalTransition;
import com.createchance.imageeditor.transitions.DirectionalWarpTransition;
import com.createchance.imageeditor.transitions.DirectionalWipeTransition;
import com.createchance.imageeditor.transitions.DoomScreenTransition;
import com.createchance.imageeditor.transitions.DoorWayTransition;
import com.createchance.imageeditor.transitions.DreamyTransition;
import com.createchance.imageeditor.transitions.DreamyZoomTransition;
import com.createchance.imageeditor.transitions.FadeColorTransition;
import com.createchance.imageeditor.transitions.FadeGrayScaleTransition;
import com.createchance.imageeditor.transitions.FadeTransition;
import com.createchance.imageeditor.transitions.FlyEyeTransition;
import com.createchance.imageeditor.transitions.GlitchDisplaceTransition;
import com.createchance.imageeditor.transitions.GlitchMemoriesTransition;
import com.createchance.imageeditor.transitions.GridFlipTransition;
import com.createchance.imageeditor.transitions.HeartTransition;
import com.createchance.imageeditor.transitions.HexagonalTransition;
import com.createchance.imageeditor.transitions.InvertedPageCurlTransition;
import com.createchance.imageeditor.transitions.KaleidoScopeTransition;
import com.createchance.imageeditor.transitions.LinearBlurTransition;
import com.createchance.imageeditor.transitions.LuminanceMeltTransition;
import com.createchance.imageeditor.transitions.MorphTransition;
import com.createchance.imageeditor.transitions.MosaicTransition;
import com.createchance.imageeditor.transitions.MultiplyBlendTransition;
import com.createchance.imageeditor.transitions.PerlinTransition;
import com.createchance.imageeditor.transitions.PinWheelTransition;
import com.createchance.imageeditor.transitions.PixelizeTransition;
import com.createchance.imageeditor.transitions.PolarFunctionTransition;
import com.createchance.imageeditor.transitions.PolkaDotsCurtainTransition;
import com.createchance.imageeditor.transitions.RadialTransition;
import com.createchance.imageeditor.transitions.RandomSquaresTransition;
import com.createchance.imageeditor.transitions.RippleTransition;
import com.createchance.imageeditor.transitions.RotateScaleFadeTransition;
import com.createchance.imageeditor.transitions.SimpleZoomTransition;
import com.createchance.imageeditor.transitions.SquaresWireTransition;
import com.createchance.imageeditor.transitions.SqueezeTransition;
import com.createchance.imageeditor.transitions.StereoViewerTransition;
import com.createchance.imageeditor.transitions.SwapTransition;
import com.createchance.imageeditor.transitions.SwirlTransition;
import com.createchance.imageeditor.transitions.UndulatingBurnOutTransition;
import com.createchance.imageeditor.transitions.WaterDropTransition;
import com.createchance.imageeditor.transitions.WindTransition;
import com.createchance.imageeditor.transitions.WindowBlindsTransition;
import com.createchance.imageeditor.transitions.WindowSliceTransition;
import com.createchance.imageeditor.transitions.WipeDownTransition;
import com.createchance.imageeditor.transitions.WipeLeftTransition;
import com.createchance.imageeditor.transitions.WipeRightTransition;
import com.createchance.imageeditor.transitions.WipeUpTransition;
import com.createchance.imageeditor.transitions.ZoomInCirclesTransition;

public class TransUtils {
    public static AbstractTransition getTransitionById(int id) {
        AbstractTransition transition = null;
        switch (id){
            case 0:
                transition = new WindowSliceTransition();
                break;
            case 1:
                transition = new InvertedPageCurlTransition();
                break;
            case 2:
                transition = new AngularTransition();
                break;
            case 3:
                transition = new BounceTransition();
                break;
            case 4:
                transition = new BowTieHorizontalTransition();
                break;
            case 5:
                transition = new BowTieVerticalTransition();
                break;
            case 6:
                transition = new BurnTransition();
                break;
            case 7:
                transition = new ButterflyWaveScrawlerTransition();
                break;
            case 8:
                transition = new CannabisLeafTransition();
                break;
            case 9:
                transition = new CircleTransition();
                break;
            case 10:
                transition = new CircleCropTransition();
                break;
            case 11:
                transition = new CircleOpenTransition();
                break;
            case 12:
                transition = new ColorPhaseTransition();
                break;
            case 13:
                transition = new ColorDistanceTransition();
                break;
            case 14:
                transition = new CrazyParametricFunTransition();
                break;
            case 15:
                transition = new CrossHatchTransition();
                break;
            case 16:
                transition = new CrossWarpTransition();
                break;
            case 17:
                transition = new CrossZoomTransition();
                break;
            case 18:
                transition = new CubeTransition();
                break;
            case 19:
                transition = new DirectionalTransition();
                break;
            case 20:
                transition = new DirectionalWarpTransition();
                break;
            case 21:
                transition = new DirectionalWipeTransition();
                break;
            case 22:
                transition = new DoomScreenTransition();
                break;
            case 23:
                transition = new DoorWayTransition();
                break;
            case 24:
                transition = new DreamyTransition();
                break;
            case 25:
                transition = new DreamyZoomTransition();
                break;
            case 26:
                transition = new FadeTransition();
                break;
            case 27:
                transition = new FadeColorTransition();
                break;
            case 28:
                transition = new FadeGrayScaleTransition();
                break;
            case 29:
                transition = new FlyEyeTransition();
                break;
            case 30:
                transition = new GlitchDisplaceTransition();
                break;
            case 31:
                transition = new GlitchMemoriesTransition();
                break;
            case 32:
                transition = new GridFlipTransition();
                break;
            case 33:
                transition = new HeartTransition();
                break;
            case 34:
                transition = new HexagonalTransition();
                break;
            case 35:
                transition = new KaleidoScopeTransition();
                break;
            case 36:
                transition = new LinearBlurTransition();
                break;
            case 37:
                transition = new LuminanceMeltTransition();
                break;
            case 38:
                transition = new MorphTransition();
                break;
            case 39:
                transition = new MosaicTransition();
                break;
            case 40:
                transition = new MultiplyBlendTransition();
                break;
            case 41:
                transition = new PerlinTransition();
                break;
            case 42:
                transition = new PinWheelTransition();
                break;
            case 43:
                transition = new PixelizeTransition();
                break;
            case 44:
                transition = new PolarFunctionTransition();
                break;
            case 45:
                transition = new PolkaDotsCurtainTransition();
                break;
            case 46:
                transition = new RadialTransition();
                break;
            case 47:
                transition = new RandomSquaresTransition();
                break;
            case 48:
                transition = new RippleTransition();
                break;
            case 49:
                transition = new RotateScaleFadeTransition();
                break;
            case 50:
                transition = new SimpleZoomTransition();
                break;
            case 51:
                transition = new SquaresWireTransition();
                break;
            case 52:
                transition = new SqueezeTransition();
                break;
            case 53:
                transition = new StereoViewerTransition();
                break;
            case 54:
                transition = new SwapTransition();
                break;
            case 55:
                transition = new SwirlTransition();
                break;
            case 56:
                transition = new UndulatingBurnOutTransition();
                break;
            case 57:
                transition = new WaterDropTransition();
                break;
            case 58:
                transition = new WindTransition();
                break;
            case 59:
                transition = new WindowBlindsTransition();
                break;
            case 60:
                transition = new WipeDownTransition();
                break;
            case 61:
                transition = new WipeUpTransition();
                break;
            case 62:
                transition = new WipeLeftTransition();
                break;
            case 63:
                transition = new WipeRightTransition();
                break;
            case 64:
                transition = new ZoomInCirclesTransition();
                break;
            default:
                break;
        }

        return transition;
    }

}
