package com.avaliacao.guiainstrumentos;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.ByteArrayOutputStream;


// Conversoes da foto de perfil, que e gravada no Room como byte[] (BLOB).
public final class ImagemUtil {

    private static final int QUALIDADE_JPEG = 85;

    private ImagemUtil() {
    }

    public static byte[] paraBytes(Bitmap bitmap) {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALIDADE_JPEG, saida);
        return saida.toByteArray();
    }

    public static Bitmap paraBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // Miniatura redonda para a Toolbar.
    public static RoundedBitmapDrawable avatar(Resources recursos, byte[] foto, int tamanhoPx) {
        Bitmap quadrada = ThumbnailUtils.extractThumbnail(paraBitmap(foto), tamanhoPx, tamanhoPx);
        RoundedBitmapDrawable avatar = RoundedBitmapDrawableFactory.create(recursos, quadrada);
        avatar.setCircular(true);
        return avatar;
    }
}
