package com.y271727uy.ranch_festival.season;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

/**
 * 封装 Serene Seasons API 的工具类。
 * 这里只保留当前实际使用的季节 / 子季节 / 日数查询入口。
 */
public final class SeasonApiHelper {

    private static Class<?> seasonHelperClass;
    private static Method getCurrentSubSeasonMethod;
    private static Method getDayOfSeasonMethod;
    private static Method getCurrentSeasonMethod;
    private static boolean initialized;
    private static boolean apiAvailable;

    static {
        initializeReflection();
    }

    private SeasonApiHelper() {
    }

    private static void initializeReflection() {
        if (initialized) {
            return;
        }

        if (hasSereneSeasons()) {
            // continue
        } else {
            initialized = true;
            return;
        }

        try {
            String[] possibleClassNames = {
                    "sereneseasons.helper.SeasonHelper",
                    "sereneseasons.api.season.SeasonHelper",
                    "sereneseasons.core.CommonProxy",
                    "sereneseasons.asm.ASMQueries"
            };

            for (String className : possibleClassNames) {
                try {
                    seasonHelperClass = Class.forName(className);
                    RanchFestivalMod.LOGGER.debug("Found Serene Seasons class: {}", className);
                    break;
                } catch (ClassNotFoundException ignored) {
                    // Try next class name.
                }
            }

            if (seasonHelperClass != null) {
                String[] possibleGetCurrentSubSeasonMethods = {"getCurrentSubSeason", "getSubSeason"};
                String[] possibleGetDayOfSeasonMethods = {"getDayOfSeason", "getSeasonDay"};
                String[] possibleGetCurrentSeasonMethods = {"getCurrentSeason", "getSeason"};

                for (String methodName : possibleGetCurrentSubSeasonMethods) {
                    try {
                        getCurrentSubSeasonMethod = seasonHelperClass.getMethod(methodName);
                        break;
                    } catch (NoSuchMethodException e) {
                        try {
                            getCurrentSubSeasonMethod = seasonHelperClass.getMethod(methodName, Level.class);
                            break;
                        } catch (NoSuchMethodException ignored) {
                            // Try next method name.
                        }
                    }
                }

                for (String methodName : possibleGetDayOfSeasonMethods) {
                    try {
                        getDayOfSeasonMethod = seasonHelperClass.getMethod(methodName);
                        break;
                    } catch (NoSuchMethodException e) {
                        try {
                            getDayOfSeasonMethod = seasonHelperClass.getMethod(methodName, Level.class);
                            break;
                        } catch (NoSuchMethodException ignored) {
                            // Try next method name.
                        }
                    }
                }

                for (String methodName : possibleGetCurrentSeasonMethods) {
                    try {
                        getCurrentSeasonMethod = seasonHelperClass.getMethod(methodName);
                        break;
                    } catch (NoSuchMethodException e) {
                        try {
                            getCurrentSeasonMethod = seasonHelperClass.getMethod(methodName, Level.class);
                            break;
                        } catch (NoSuchMethodException ignored) {
                            // Try next method name.
                        }
                    }
                }

                apiAvailable = getCurrentSubSeasonMethod != null
                        && getDayOfSeasonMethod != null
                        && getCurrentSeasonMethod != null;

                if (!apiAvailable) {
                    RanchFestivalMod.LOGGER.warn("Serene Seasons reflection helper initialized partially; season info APIs are unavailable");
                }
            }
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error initializing Serene Seasons API", e);
        }

        initialized = true;
    }

    /**
     * 检查 Serene Seasons 是否可用。
     */
    public static boolean hasSereneSeasons() {
        boolean loaded = ModList.get().isLoaded("sereneseasons");
        if (!loaded) {
            RanchFestivalMod.LOGGER.debug("Serene Seasons is not loaded");
        } else {
            RanchFestivalMod.LOGGER.debug("Serene Seasons is loaded");
        }
        return loaded;
    }

    /**
     * 获取当前子季节。
     */
    public static Object getCurrentSubSeason() {
        initializeReflection();

        if (!apiAvailable || getCurrentSubSeasonMethod == null) {
            RanchFestivalMod.LOGGER.debug("getCurrentSubSeason method is not available");
            return null;
        }

        try {
            Object result;
            if (getCurrentSubSeasonMethod.getParameterCount() == 0) {
                result = getCurrentSubSeasonMethod.invoke(null);
            } else {
                result = getCurrentSubSeasonMethod.invoke(null, (Level) null);
            }
            return result;
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error calling getCurrentSubSeason", e);
            return null;
        }
    }

    /**
     * 获取当前季节的第几天。
     */
    public static int getDayOfSeason() {
        initializeReflection();

        if (!apiAvailable || getDayOfSeasonMethod == null) {
            RanchFestivalMod.LOGGER.debug("getDayOfSeason method is not available");
            return -1;
        }

        try {
            Object result;
            if (getDayOfSeasonMethod.getParameterCount() == 0) {
                result = getDayOfSeasonMethod.invoke(null);
            } else {
                result = getDayOfSeasonMethod.invoke(null, (Level) null);
            }

            if (result instanceof Integer day) {
                return day;
            }

            RanchFestivalMod.LOGGER.warn("getDayOfSeason returned unexpected type: {}", result != null ? result.getClass() : "null");
            return -1;
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error calling getDayOfSeason", e);
            return -1;
        }
    }

    /**
     * 获取当前季节。
     */
    public static Object getCurrentSeason() {
        initializeReflection();

        if (!apiAvailable || getCurrentSeasonMethod == null) {
            RanchFestivalMod.LOGGER.debug("getCurrentSeason method is not available");
            return null;
        }

        try {
            Object result;
            if (getCurrentSeasonMethod.getParameterCount() == 0) {
                result = getCurrentSeasonMethod.invoke(null);
            } else {
                result = getCurrentSeasonMethod.invoke(null, (Level) null);
            }
            return result;
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error calling getCurrentSeason", e);
            return null;
        }
    }

    /**
     * 获取当前子季节的字符串表示。
     */
    public static String getCurrentSubSeasonString() {
        Object subSeason = getCurrentSubSeason();
        return subSeason != null ? subSeason.toString() : "UNKNOWN";
    }

    /**
     * 获取当前季节的字符串表示。
     */
    public static String getCurrentSeasonString() {
        Object season = getCurrentSeason();
        return season != null ? season.toString() : "UNKNOWN";
    }
}