package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils {
   private static final Pattern field_76339_a = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

   public static boolean func_151246_b(String p_151246_0_) {
      return org.apache.commons.lang3.StringUtils.isEmpty(p_151246_0_);
   }
}
