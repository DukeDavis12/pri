// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.pri;

import static org.sdo.pri.Matchers.expect;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Codec for {@link HashMac}.
 */
abstract class HashMacCodec {

  static class HashMacDecoder implements ProtocolDecoder<HashMac> {

    @Override
    public HashMac decode(CharBuffer in) throws IOException {

      final Uint8Codec.Decoder u8d = new Uint8Codec().new Decoder();

      Matchers.expect(in, Json.BEGIN_ARRAY);
      final int length = u8d.apply(in).intValue();

      Matchers.expect(in, Json.COMMA);
      final int hashType = u8d.apply(in).intValue();

      Matchers.expect(in, Json.COMMA);
      final ByteBuffer hash = new ByteArrayCodec().decoder().apply(in);

      Matchers.expect(in, Json.END_ARRAY);

      if (hash.remaining() != length) {
        throw new IOException("length mismatch");
      }

      return new HashMac(MacType.fromNumber(hashType), hash);
    }
  }

  static class HashMacEncoder implements ProtocolEncoder<HashMac> {

    @Override
    public void encode(Writer out, HashMac val) throws IOException {

      final Uint8Codec.Encoder u8e = new Uint8Codec().new Encoder();
      final ByteBuffer hash = val.getHash();

      out.write(Json.BEGIN_ARRAY);
      u8e.apply(out, hash.remaining());

      out.write(Json.COMMA);
      u8e.apply(out, val.getType().getCode());

      out.write(Json.COMMA);
      new ByteArrayCodec().encoder().apply(out, hash);

      out.write(Json.END_ARRAY);
    }
  }
}
