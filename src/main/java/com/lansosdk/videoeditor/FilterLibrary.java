/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lansosdk.videoeditor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.lansosdk.box.BitmapLoader;

import jp.co.cyberagent.lansongsdk.gpuimage.GPUImage3x3ConvolutionFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImage3x3TextureSamplingFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageAddBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageCGAColorspaceFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageChromaKeyBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageColorBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageColorBurnBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageColorDodgeBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageCrosshatchFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageDarkenBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageDifferenceBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageDivideBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageExclusionBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageFalseColorFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageHalftoneFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageHardLightBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageHueBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageLaplacianFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageLevelsFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageLightenBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageLinearBurnBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageLookupFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageLuminosityBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageMultiplyBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageNormalBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageOpacityFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageOverlayBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSaturationBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageScreenBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSoftLightBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSourceOverBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSubtractBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageSwirlFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageTwoInputFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageVignetteFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.GPUImageWhiteBalanceFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IF1977Filter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFAmaroFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFBrannanFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFEarlybirdFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFHefeFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFHudsonFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFInkwellFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFLomofiFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFLordKelvinFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFNashvilleFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFRiseFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFSierraFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFSutroFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFToasterFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFValenciaFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFWaldenFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.IFXproIIFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.LanSongBeautyAdvanceFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.LanSongBlackMaskBlendFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.LanSongBlurFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.LanSongBulgeDistortionFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.LanSongDistortionPinchFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.LanSongDistortionStretchFilter;
import jp.co.cyberagent.lansongsdk.gpuimage.LanSongMaskBlendFilter;

public class FilterLibrary {

    private static FilterList filterList = null;

    public static FilterList getFilterList() {
        if (filterList == null) {
            showAllFilter();
        }
        return filterList;
    }

    public static void showAllFilter() {
        filterList = new FilterList();
        // 2017年11月10日09:38:40 83
        filterList.addFilter("无", FilterType.NONE);
        filterList.addFilter("美颜", FilterType.BEAUTIFUL);

        filterList.addFilter("1AMARO", FilterType.AMARO);
        filterList.addFilter("2RISE", FilterType.RISE);
        filterList.addFilter("3HUDSON", FilterType.HUDSON);
        filterList.addFilter("4XPROII", FilterType.XPROII);
        filterList.addFilter("5SIERRA", FilterType.SIERRA);
        filterList.addFilter("6LOMOFI", FilterType.LOMOFI);
        filterList.addFilter("7EARLYBIRD", FilterType.EARLYBIRD);
        filterList.addFilter("8SUTRO", FilterType.SUTRO);
        filterList.addFilter("9TOASTER", FilterType.TOASTER);
        filterList.addFilter("10BRANNAN", FilterType.BRANNAN);
        filterList.addFilter("11INKWELL", FilterType.INKWELL);
        filterList.addFilter("12WALDEN", FilterType.WALDEN);
        filterList.addFilter("13HEFE", FilterType.HEFE);
        filterList.addFilter("14VALENCIA", FilterType.VALENCIA);
        filterList.addFilter("15NASHVILLE", FilterType.NASHVILLE);
        filterList.addFilter("16if1977", FilterType.IF1977);
        filterList.addFilter("17LORDKELVIN", FilterType.LORDKELVIN);

        filterList.addFilter("黑色抠图", FilterType.LanSongBLACKMASK);
        filterList.addFilter("区域透明", FilterType.LanSongMASK);

        filterList.addFilter("Invert负片", FilterType.INVERT);
        filterList.addFilter("Pixelation像素方块", FilterType.PIXELATION);

        filterList.addFilter("Vignette加轮廓", FilterType.VIGNETTE);
        filterList.addFilter("Haze加减雾", FilterType.HAZE);
        filterList.addFilter("Glass Sphere玻璃球效果", FilterType.GLASS_SPHERE);
        filterList.addFilter("Sphere Refraction球面折射",
                FilterType.SPHERE_REFRACTION);

        // 新增
        filterList.addFilter("PINCH_DISTORTION", FilterType.PINCH_DISTORTION);
        filterList.addFilter("STRETCH_DISTORTION",
                FilterType.STRETCH_DISTORTION);
        filterList.addFilter("Bulge Distortion凸凹调节",
                FilterType.BULGE_DISTORTION);

        filterList.addFilter("Brightness图像亮度", FilterType.BRIGHTNESS);

        filterList.addFilter("高斯模糊", FilterType.LanSongBLUR);

        filterList.addFilter("Swirl旋涡", FilterType.SWIRL);
        filterList.addFilter("Posterize色调分离", FilterType.POSTERIZE);
        filterList.addFilter("Sepia复古", FilterType.SEPIA);

        filterList.addFilter("Highlight Shadow阴影高亮",
                FilterType.HIGHLIGHT_SHADOW);
        filterList.addFilter("Monochrome单色", FilterType.MONOCHROME);
        filterList.addFilter("White Balance白平衡", FilterType.WHITE_BALANCE);
        filterList.addFilter("Exposure曝光度", FilterType.EXPOSURE);
        filterList.addFilter("Hue色调", FilterType.HUE);
        filterList.addFilter("Gamma伽玛", FilterType.GAMMA);

        filterList.addFilter("False Color", FilterType.FALSE_COLOR);
        filterList.addFilter("Color Balance颜色平衡", FilterType.COLOR_BALANCE);
        filterList.addFilter("Levels Min (Mid Adjust)暗色调节",
                FilterType.LEVELS_FILTER_MIN);
        filterList
                .addFilter("Lookup (Amatorka)查找表", FilterType.LOOKUP_AMATORKA);
        filterList.addFilter("Crosshatch交叉阴影网格", FilterType.CROSSHATCH);

        filterList.addFilter("CGA Color Space", FilterType.CGA_COLORSPACE);
        filterList.addFilter("Kuwahara", FilterType.KUWAHARA);
        filterList.addFilter("Halftone棉麻", FilterType.HALFTONE);

        filterList.addFilter("Opacity透明度", FilterType.OPACITY);
        filterList.addFilter("RGB颜色调整", FilterType.RGB);

        filterList.addFilter("Grayscale灰度", FilterType.GRAYSCALE);
        filterList.addFilter("Contrast对比度", FilterType.CONTRAST);
        filterList.addFilter("Saturation饱和度", FilterType.SATURATION);

        filterList.addFilter("Blend (Difference)", FilterType.BLEND_DIFFERENCE);
        filterList.addFilter("Blend (Source Over)",
                FilterType.BLEND_SOURCE_OVER);
        filterList.addFilter("Blend (Color Burn)", FilterType.BLEND_COLOR_BURN);
        filterList.addFilter("Blend (Color Dodge)",
                FilterType.BLEND_COLOR_DODGE);
        filterList.addFilter("Blend (Darken)", FilterType.BLEND_DARKEN);
        filterList.addFilter("Blend (Dissolve)", FilterType.BLEND_DISSOLVE);
        filterList.addFilter("Blend (Exclusion)", FilterType.BLEND_EXCLUSION);
        filterList.addFilter("Blend (Hard Light)", FilterType.BLEND_HARD_LIGHT);
        filterList.addFilter("Blend (Lighten)", FilterType.BLEND_LIGHTEN);
        filterList.addFilter("Blend (Add)", FilterType.BLEND_ADD);
        filterList.addFilter("Blend (Divide)", FilterType.BLEND_DIVIDE);
        filterList.addFilter("Blend (Multiply)", FilterType.BLEND_MULTIPLY);
        filterList.addFilter("Blend (Overlay)", FilterType.BLEND_OVERLAY);
        filterList.addFilter("Blend (Screen)", FilterType.BLEND_SCREEN);
        filterList.addFilter("Blend (Alpha)", FilterType.BLEND_ALPHA);
        filterList.addFilter("Blend (Color)", FilterType.BLEND_COLOR);
        filterList.addFilter("Blend (Hue)", FilterType.BLEND_HUE);
        filterList.addFilter("Blend (Saturation)", FilterType.BLEND_SATURATION);
        filterList.addFilter("Blend (Luminosity)", FilterType.BLEND_LUMINOSITY);
        filterList.addFilter("Blend (Linear Burn)",
                FilterType.BLEND_LINEAR_BURN);
        filterList.addFilter("Blend (Soft Light)", FilterType.BLEND_SOFT_LIGHT);
        filterList.addFilter("Blend (Subtract)", FilterType.BLEND_SUBTRACT);
        filterList.addFilter("Blend (Chroma Key)", FilterType.BLEND_CHROMA_KEY);
        filterList.addFilter("Blend (Normal)", FilterType.BLEND_NORMAL);

        filterList.addFilter("EMBOSS粗麻", FilterType.EMBOSS);
        filterList.addFilter("3x3转换", FilterType.THREE_X_THREE_CONVOLUTION);
        filterList.addFilter("Laplacian浮雕", FilterType.LAPLACIAN);
        filterList.addFilter("Toon", FilterType.TOON);
    }

    public static void showDialog(final Context context,
                                  final OnGpuImageFilterChosenListener listener) {
        if (filterList == null) {
            showAllFilter();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a filter(total:" + filterList.names.size()
                + " )");
        builder.setItems(
                filterList.names.toArray(new String[filterList.names.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                                        final int item) {
                        listener.onGpuImageFilterChosenListener(
                                getFilterObject(context,
                                        filterList.filters.get(item)),
                                filterList.names.get(item));
                    }
                });
        builder.create().show();
    }

    public static GPUImageFilter getFilterObject(final Context context,
                                                 final FilterType type) {
        switch (type) {
            case NONE:
                return null;
            /**
             * 注意: 以下五种美颜级别,仅仅是列举,详情可看@BeautylLevel ; 实际您可以任意组合.
             * LanSongBeautyLevel1--5是不存在的滤镜, 仅仅是为了兼容其他滤镜而做的参考.
             */
            case BEAUTIFUL:
                return new LanSongBeautyAdvanceFilter(); // 美颜默认全开.
            // case BEAUTIFUL2: //白皙美颜默认不再使用.
            // return new LanSongBeautyWhiteFilter();
            case CONTRAST:
                return new GPUImageContrastFilter(2.0f);
            case GAMMA:
                return new GPUImageGammaFilter(2.0f);
            case INVERT:
                return new GPUImageColorInvertFilter();
            case PIXELATION:
                return new GPUImagePixelationFilter();
            case HUE:
                return new GPUImageHueFilter(90.0f);
            case BRIGHTNESS:
                return new GPUImageBrightnessFilter(0.5f);
            case GRAYSCALE:
                return new GPUImageGrayscaleFilter();
            case SEPIA:
                return new GPUImageSepiaFilter();
            case POSTERIZE:
                return new GPUImagePosterizeFilter();
            case SATURATION:
                return new GPUImageSaturationFilter(1.0f);
            case EXPOSURE:
                return new GPUImageExposureFilter(0.0f);
            case HIGHLIGHT_SHADOW:
                return new GPUImageHighlightShadowFilter(0.0f, 1.0f);
            case MONOCHROME:
                return new GPUImageMonochromeFilter(1.0f, new float[]{0.6f,
                        0.45f, 0.3f, 1.0f});
            case OPACITY:
                return new GPUImageOpacityFilter(1.0f);
            case RGB:
                return new GPUImageRGBFilter(1.0f, 1.0f, 1.0f);
            case WHITE_BALANCE:
                return new GPUImageWhiteBalanceFilter(5000.0f, 0.0f);
            case LanSongBLUR:
                return new LanSongBlurFilter();
            case VIGNETTE:
                PointF centerPoint = new PointF();
                centerPoint.x = 0.5f;
                centerPoint.y = 0.5f;
                return new GPUImageVignetteFilter(centerPoint, new float[]{0.0f,
                        0.0f, 0.0f}, 0.3f, 0.75f);
            case LanSongBLACKMASK:
                /**
                 * 这个滤镜的效果是: 把输入源的某区域
                 * 处理成透明(如果bitmap有灰色的毛刺,则可能扣除的不规则,一般使用在用代码生成的bitmap图片中,
                 * 不适用用photoshop等做成的图片).
                 *
                 * 这个滤镜的效果是: 把传递进来的bitmap图片, 从中心叠加到输入源上, 并判断Bitmap中黑色像素RGB中R的值，
                 * 如果等于0, 则设置输入源对应的像素为透明， 如果不等于0，则把R替换输入源像素中的RGBA中A，从而实现半透明等效果。
                 */
                return createBlendFilter(context, LanSongBlackMaskBlendFilter.class);
            case LanSongMASK:
                /**
                 * 这个滤镜的效果是: 把输入源的某区域 处理成透明.
                 *
                 * 详情是: 把一张有透明区域的图片, 叠加到 输入源的中心位置上, 图片中有透明的地方,则把输入源的对应的地方,透明处理.
                 * 等于是把输入源中的一部分抠去.
                 */
                return createBlendFilter(context, LanSongMaskBlendFilter.class);

            case BLEND_DIFFERENCE:
                return createBlendFilter(context,
                        GPUImageDifferenceBlendFilter.class);
            case BLEND_SOURCE_OVER:
                return createBlendFilter(context,
                        GPUImageSourceOverBlendFilter.class);
            case BLEND_COLOR_BURN:
                return createBlendFilter(context,
                        GPUImageColorBurnBlendFilter.class);
            case BLEND_COLOR_DODGE:
                return createBlendFilter(context,
                        GPUImageColorDodgeBlendFilter.class);
            case BLEND_DARKEN:
                return createBlendFilter(context, GPUImageDarkenBlendFilter.class);
            case BLEND_DISSOLVE:
                return createBlendFilter(context, GPUImageDissolveBlendFilter.class);
            case BLEND_EXCLUSION:
                return createBlendFilter(context,
                        GPUImageExclusionBlendFilter.class);
            case BLEND_HARD_LIGHT:
                return createBlendFilter(context,
                        GPUImageHardLightBlendFilter.class);
            case BLEND_LIGHTEN:
                return createBlendFilter(context, GPUImageLightenBlendFilter.class);
            case BLEND_ADD:
                return createBlendFilter(context, GPUImageAddBlendFilter.class);
            case BLEND_DIVIDE:
                return createBlendFilter(context, GPUImageDivideBlendFilter.class);
            case BLEND_MULTIPLY:
                return createBlendFilter(context, GPUImageMultiplyBlendFilter.class);
            case BLEND_OVERLAY:
                return createBlendFilter(context, GPUImageOverlayBlendFilter.class);
            case BLEND_SCREEN:
                return createBlendFilter(context, GPUImageScreenBlendFilter.class);
            case BLEND_ALPHA:
                return createBlendFilter(context, GPUImageAlphaBlendFilter.class);
            case BLEND_COLOR:
                return createBlendFilter(context, GPUImageColorBlendFilter.class);
            case BLEND_HUE:
                return createBlendFilter(context, GPUImageHueBlendFilter.class);
            case BLEND_SATURATION:
                return createBlendFilter(context,
                        GPUImageSaturationBlendFilter.class);
            case BLEND_LUMINOSITY:
                return createBlendFilter(context,
                        GPUImageLuminosityBlendFilter.class);
            case BLEND_LINEAR_BURN:
                return createBlendFilter(context,
                        GPUImageLinearBurnBlendFilter.class);
            case BLEND_SOFT_LIGHT:
                return createBlendFilter(context,
                        GPUImageSoftLightBlendFilter.class);
            case BLEND_SUBTRACT:
                return createBlendFilter(context, GPUImageSubtractBlendFilter.class);
            case BLEND_CHROMA_KEY:
                return createBlendFilter(context,
                        GPUImageChromaKeyBlendFilter.class);
            case BLEND_NORMAL:
                return createBlendFilter(context, GPUImageNormalBlendFilter.class);

            case LOOKUP_AMATORKA:
                GPUImageLookupFilter amatorka = new GPUImageLookupFilter();
                String var3 = "assets://LSResource/lookup_amatorka.png";
                amatorka.setBitmap(BitmapLoader.load(context, var3, 0, 0));
                return amatorka;
            case CROSSHATCH:
                return new GPUImageCrosshatchFilter();
            case CGA_COLORSPACE:
                return new GPUImageCGAColorspaceFilter();
            case KUWAHARA:
                return new GPUImageKuwaharaFilter();

            case BULGE_DISTORTION:
                /**
                 * 凸凹 可以设置凸凹的地方, 凸凹的范围, 凸起还是凹下.
                 */
                return new LanSongBulgeDistortionFilter();

            // 新增
            case PINCH_DISTORTION:
                return new LanSongDistortionPinchFilter();
            case STRETCH_DISTORTION:
                return new LanSongDistortionStretchFilter();

            case GLASS_SPHERE:
                return new GPUImageGlassSphereFilter();
            case HAZE:
                return new GPUImageHazeFilter();
            case SPHERE_REFRACTION:
                return new GPUImageSphereRefractionFilter();
            case SWIRL:
                return new GPUImageSwirlFilter();
            case FALSE_COLOR:
                return new GPUImageFalseColorFilter();
            case COLOR_BALANCE:
                return new GPUImageColorBalanceFilter();
            case LEVELS_FILTER_MIN:
                GPUImageLevelsFilter levelsFilter = new GPUImageLevelsFilter();
                levelsFilter.setMin(0.0f, 3.0f, 1.0f);
                return levelsFilter;
            case HALFTONE:
                return new GPUImageHalftoneFilter();
            case AMARO:
                return new IFAmaroFilter(context);
            case RISE:
                return new IFRiseFilter(context);
            case HUDSON:
                return new IFHudsonFilter(context);
            case XPROII:
                return new IFXproIIFilter(context);
            case SIERRA:
                return new IFSierraFilter(context);
            case LOMOFI:
                return new IFLomofiFilter(context);
            case EARLYBIRD:
                return new IFEarlybirdFilter(context);
            case SUTRO:
                return new IFSutroFilter(context);
            case TOASTER:
                return new IFToasterFilter(context);
            case BRANNAN:
                return new IFBrannanFilter(context);
            case INKWELL:
                return new IFInkwellFilter(context);
            case WALDEN:
                return new IFWaldenFilter(context);
            case HEFE:
                return new IFHefeFilter(context);
            case VALENCIA:
                return new IFValenciaFilter(context);
            case NASHVILLE:
                return new IFNashvilleFilter(context);
            case LORDKELVIN:
                return new IFLordKelvinFilter(context);
            case IF1977:
                return new IF1977Filter(context);

            /* 2017年8月5日18:11:17新增 */
            case EMBOSS:
                return new GPUImageEmbossFilter();
            case THREE_X_THREE_CONVOLUTION:
                GPUImage3x3ConvolutionFilter convolution = new GPUImage3x3ConvolutionFilter();
                convolution.setConvolutionKernel(new float[]{-1.0f, 0.0f, 1.0f,
                        -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f});
                return convolution;
            case LAPLACIAN:
                return new GPUImageLaplacianFilter();
            case TOON:
                return new GPUImageToonFilter();
            default:
                throw new IllegalStateException("No filter of that type!");
        }

    }

    private static GPUImageFilter createBlendFilter(Context context,
                                                    Class<? extends GPUImageTwoInputFilter> filterClass) {
        try {
            GPUImageTwoInputFilter filter = filterClass.newInstance();
            String var3 = "assets://LSResource/ic_launcher.png";
            filter.setBitmap(BitmapLoader.load(context, var3, 0, 0)); // 这里用默认图片举例.
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum FilterType {
        NONE, BEAUTIFUL, BEAUTIFUL2, LanSongBLACKMASK, LanSongMASK, LanSongBLUR, CONTRAST, GRAYSCALE, SEPIA,
        POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, PIXELATION, SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME,
        OPACITY, RGB, WHITE_BALANCE, VIGNETTE, BLEND_COLOR_BURN, BLEND_COLOR_DODGE, BLEND_DARKEN, BLEND_DIFFERENCE,
        BLEND_DISSOLVE, BLEND_EXCLUSION, BLEND_SOURCE_OVER, BLEND_HARD_LIGHT, BLEND_LIGHTEN, BLEND_ADD, BLEND_DIVIDE,
        BLEND_MULTIPLY, BLEND_OVERLAY, BLEND_SCREEN, BLEND_ALPHA, BLEND_COLOR, BLEND_HUE, BLEND_SATURATION,
        BLEND_LUMINOSITY, BLEND_LINEAR_BURN, BLEND_SOFT_LIGHT, BLEND_SUBTRACT, BLEND_CHROMA_KEY, BLEND_NORMAL,
        LOOKUP_AMATORKA, CROSSHATCH, CGA_COLORSPACE, KUWAHARA, BULGE_DISTORTION, PINCH_DISTORTION,
        STRETCH_DISTORTION, GLASS_SPHERE, HAZE, SPHERE_REFRACTION, SWIRL, FALSE_COLOR, COLOR_BALANCE,
        LEVELS_FILTER_MIN, HALFTONE,

        /* 新增 */
        EMBOSS, THREE_X_THREE_CONVOLUTION, LAPLACIAN, TOON,

        AMARO, RISE, HUDSON, XPROII, SIERRA, LOMOFI, EARLYBIRD, SUTRO, TOASTER, BRANNAN, INKWELL, WALDEN, HEFE,
        VALENCIA, NASHVILLE, IF1977, LORDKELVIN
    }

    public interface OnGpuImageFilterChosenListener {
        void onGpuImageFilterChosenListener(GPUImageFilter filter, String name);
    }

    public static class FilterAdjuster {
        private final Adjuster<? extends GPUImageFilter> adjuster;

        public FilterAdjuster(final GPUImageFilter filter) {
            if (filter instanceof LanSongBeautyAdvanceFilter) {
                adjuster = new BeautyAdvanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSepiaFilter) {
                adjuster = new SepiaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageContrastFilter) {
                adjuster = new ContrastAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGammaFilter) {
                adjuster = new GammaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBrightnessFilter) {
                adjuster = new BrightnessAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHueFilter) {
                adjuster = new HueAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePosterizeFilter) {
                adjuster = new PosterizeAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePixelationFilter) {
                adjuster = new PixelationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSaturationFilter) {
                adjuster = new SaturationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageExposureFilter) {
                adjuster = new ExposureAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHighlightShadowFilter) {
                adjuster = new HighlightShadowAdjuster().filter(filter);
            } else if (filter instanceof GPUImageMonochromeFilter) {
                adjuster = new MonochromeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageOpacityFilter) {
                adjuster = new OpacityAdjuster().filter(filter);
            } else if (filter instanceof GPUImageRGBFilter) {
                adjuster = new RGBAdjuster().filter(filter);

            } else if (filter instanceof GPUImageWhiteBalanceFilter) {
                adjuster = new WhiteBalanceAdjuster().filter(filter);

            } else if (filter instanceof LanSongBlurFilter) {
                adjuster = new LanSongBlurFilterAdjuster().filter(filter);

            } else if (filter instanceof GPUImageVignetteFilter) {
                adjuster = new VignetteAdjuster().filter(filter);
            } else if (filter instanceof GPUImageDissolveBlendFilter) {
                adjuster = new DissolveBlendAdjuster().filter(filter);
            } else if (filter instanceof GPUImageCrosshatchFilter) {
                adjuster = new CrosshatchBlurAdjuster().filter(filter);
            } else if (filter instanceof LanSongBulgeDistortionFilter) {
                adjuster = new BulgeDistortionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGlassSphereFilter) {
                adjuster = new GlassSphereAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHazeFilter) {
                adjuster = new HazeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSphereRefractionFilter) {
                adjuster = new SphereRefractionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSwirlFilter) {
                adjuster = new SwirlAdjuster().filter(filter);
            } else if (filter instanceof GPUImageColorBalanceFilter) {
                adjuster = new ColorBalanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageLevelsFilter) {
                adjuster = new LevelsMinMidAdjuster().filter(filter);
            }
            // 2017年8月5日17:56:16 新增.
            else if (filter instanceof GPUImageEmbossFilter) {
                adjuster = new EmbossAdjuster().filter(filter);
            } else if (filter instanceof GPUImage3x3TextureSamplingFilter) {
                adjuster = new GPU3x3TextureAdjuster().filter(filter);
            } else {

                adjuster = null;
            }
        }

        public boolean canAdjust() {
            return adjuster != null;
        }

        public void adjust(final int percentage) {
            if (adjuster != null) {
                adjuster.adjust(percentage);
            }
        }

        private abstract class Adjuster<T extends GPUImageFilter> {
            private T filter;

            @SuppressWarnings("unchecked")
            public Adjuster<T> filter(final GPUImageFilter filter) {
                this.filter = (T) filter;
                return this;
            }

            public T getFilter() {
                return filter;
            }

            public abstract void adjust(int percentage);

            protected float range(final int percentage, final float start,
                                  final float end) {
                return (end - start) * percentage / 100.0f + start;
            }

            protected int range(final int percentage, final int start,
                                final int end) {
                return (end - start) * percentage / 100 + start;
            }
        }

        private class BeautyAdvanceAdjuster extends
                Adjuster<LanSongBeautyAdvanceFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setBeautyLevel(range(percentage, 0.0f, 1.0f));
            }
        }

        private class PixelationAdjuster extends
                Adjuster<GPUImagePixelationFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setPixel(range(percentage, 1.0f, 100.0f));
            }
        }

        private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setHue(range(percentage, 0.0f, 360.0f));
            }
        }

        private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setContrast(range(percentage, 0.0f, 2.0f));
            }
        }

        private class GammaAdjuster extends Adjuster<GPUImageGammaFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setGamma(range(percentage, 0.0f, 3.0f));
            }
        }

        private class BrightnessAdjuster extends
                Adjuster<GPUImageBrightnessFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setBrightness(range(percentage, -1.0f, 1.0f));
            }
        }

        private class SepiaAdjuster extends Adjuster<GPUImageSepiaFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 2.0f));
            }
        }

        private class PosterizeAdjuster extends
                Adjuster<GPUImagePosterizeFilter> {
            @Override
            public void adjust(final int percentage) {
                // In theorie to 256, but only first 50 are interesting
                getFilter().setColorLevels(range(percentage, 1, 50));
            }
        }

        private class SaturationAdjuster extends
                Adjuster<GPUImageSaturationFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
            }
        }

        private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setExposure(range(percentage, -10.0f, 10.0f));
            }
        }

        private class HighlightShadowAdjuster extends
                Adjuster<GPUImageHighlightShadowFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setShadows(range(percentage, 0.0f, 1.0f));
                getFilter().setHighlights(range(percentage, 0.0f, 1.0f));
            }
        }

        private class MonochromeAdjuster extends
                Adjuster<GPUImageMonochromeFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 1.0f));
                // getFilter().setColor(new float[]{0.6f, 0.45f, 0.3f, 1.0f});
            }
        }

        private class OpacityAdjuster extends Adjuster<GPUImageOpacityFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setOpacity(range(percentage, 0.0f, 1.0f));
            }
        }

        private class RGBAdjuster extends Adjuster<GPUImageRGBFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRed(range(percentage, 0.0f, 1.0f));
                // getFilter().setGreen(range(percentage, 0.0f, 1.0f));
                // getFilter().setBlue(range(percentage, 0.0f, 1.0f));
            }
        }

        private class WhiteBalanceAdjuster extends
                Adjuster<GPUImageWhiteBalanceFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setTemperature(range(percentage, 2000.0f, 8000.0f));
                // getFilter().setTint(range(percentage, -100.0f, 100.0f));
            }
        }

        private class LanSongBlurFilterAdjuster extends
                Adjuster<LanSongBlurFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setBlurFactor(range(percentage, 0.0f, 8.0f));
            }
        }

        private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setVignetteStart(range(percentage, 0.0f, 1.0f));
            }
        }

        private class DissolveBlendAdjuster extends
                Adjuster<GPUImageDissolveBlendFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setMix(range(percentage, 0.0f, 1.0f));
            }
        }

        private class CrosshatchBlurAdjuster extends
                Adjuster<GPUImageCrosshatchFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter()
                        .setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
                getFilter().setLineWidth(range(percentage, 0.0f, 0.006f));
            }
        }

        private class BulgeDistortionAdjuster extends
                Adjuster<LanSongBulgeDistortionFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
                getFilter().setScale(range(percentage, -1.0f, 1.0f));
            }
        }

        private class GlassSphereAdjuster extends
                Adjuster<GPUImageGlassSphereFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
            }
        }

        private class HazeAdjuster extends Adjuster<GPUImageHazeFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setDistance(range(percentage, -0.3f, 0.3f));
                getFilter().setSlope(range(percentage, -0.3f, 0.3f));
            }
        }

        private class SphereRefractionAdjuster extends
                Adjuster<GPUImageSphereRefractionFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
            }
        }

        private class SwirlAdjuster extends Adjuster<GPUImageSwirlFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setAngle(range(percentage, 0.0f, 2.0f));
            }
        }

        private class ColorBalanceAdjuster extends
                Adjuster<GPUImageColorBalanceFilter> {

            @Override
            public void adjust(int percentage) {
                getFilter().setMidtones(
                        new float[]{range(percentage, 0.0f, 1.0f),
                                range(percentage / 2, 0.0f, 1.0f),
                                range(percentage / 3, 0.0f, 1.0f)});
            }
        }

        private class LevelsMinMidAdjuster extends
                Adjuster<GPUImageLevelsFilter> {
            @Override
            public void adjust(int percentage) {
                getFilter().setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f);
            }
        }

        // ---------------2017年8月5日18:06:01新增的滤镜
        private class EmbossAdjuster extends Adjuster<GPUImageEmbossFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 4.0f));
            }
        }

        private class GPU3x3TextureAdjuster extends
                Adjuster<GPUImage3x3TextureSamplingFilter> {
            @Override
            public void adjust(final int percentage) {
                getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
            }
        }
    }
}