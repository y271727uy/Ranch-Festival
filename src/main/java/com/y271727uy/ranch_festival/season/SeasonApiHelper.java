package com.y271727uy.ranch_festival.season;

import com.y271727uy.ranch_festival.RanchFestivalMod;
import net.minecraftforge.fml.ModList;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;

/**
 * 封装Serene Seasons API的工具类
 * 使用反射安全地调用Serene Seasons API
 */
public class SeasonApiHelper {

    private static Class<?> seasonHelperClass;
    private static Method getServerSeasonMethod;
    private static Method getCurrentSubSeasonMethod;
    private static Method getDayOfSeasonMethod;
    private static Method getCurrentSeasonMethod;
    private static boolean initialized = false;
    private static boolean apiAvailable = false;

    static {
        initializeReflection();
    }

    /**
     * 初始化反射方法
     */
    private static void initializeReflection() {
        if (initialized) return;
        
        if (!isSereneSeasonsLoaded()) {
            initialized = true;
            return;
        }

        try {
            // 尝试不同的可能的类路径
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
                } catch (ClassNotFoundException e) {
                    // 继续尝试下一个类名
                }
            }
            
            if (seasonHelperClass != null) {
                // 尝试获取方法 - 不同版本的Serene Seasons可能有不同的API
                String[] possibleGetServerSeasonMethods = {"getServerSeason", "getWorldSeasonState"};
                String[] possibleGetCurrentSubSeasonMethods = {"getCurrentSubSeason", "getSubSeason"};
                String[] possibleGetDayOfSeasonMethods = {"getDayOfSeason", "getSeasonDay"};
                String[] possibleGetCurrentSeasonMethods = {"getCurrentSeason", "getSeason"};

                for (String methodName : possibleGetServerSeasonMethods) {
                    try {
                        getServerSeasonMethod = seasonHelperClass.getMethod(methodName);
                        break;
                    } catch (NoSuchMethodException e) {
                        try {
                            getServerSeasonMethod = seasonHelperClass.getMethod(methodName, Level.class);
                            break;
                        } catch (NoSuchMethodException ex) {
                            // 尝试下一个方法名
                        }
                    }
                }

                for (String methodName : possibleGetCurrentSubSeasonMethods) {
                    try {
                        getCurrentSubSeasonMethod = seasonHelperClass.getMethod(methodName);
                        break;
                    } catch (NoSuchMethodException e) {
                        try {
                            getCurrentSubSeasonMethod = seasonHelperClass.getMethod(methodName, Level.class);
                            break;
                        } catch (NoSuchMethodException ex) {
                            // 尝试下一个方法名
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
                        } catch (NoSuchMethodException ex) {
                            // 尝试下一个方法名
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
                        } catch (NoSuchMethodException ex) {
                            // 尝试下一个方法名
                        }
                    }
                }
                
                apiAvailable = true;
            }
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error initializing Serene Seasons API", e);
        }
        
        initialized = true;
    }

    /**
     * 检查Serene Seasons是否可用
     */
    public static boolean isSereneSeasonsLoaded() {
        boolean loaded = ModList.get().isLoaded("sereneseasons");
        if (!loaded) {
            RanchFestivalMod.LOGGER.debug("Serene Seasons is not loaded");
        } else {
            RanchFestivalMod.LOGGER.debug("Serene Seasons is loaded");
        }
        return loaded;
    }

    /**
     * 获取服务器的季节状态
     * @return 服务器的季节状态，如果Serene Seasons未加载或API不可用则返回null
     */
    public static Object getServerSeason() {
        initializeReflection(); // 确保初始化
        
        if (!apiAvailable || getServerSeasonMethod == null) {
            RanchFestivalMod.LOGGER.debug("getServerSeason method is not available");
            return null;
        }
        
        try {
            Object result;
            if (getServerSeasonMethod.getParameterCount() == 0) {
                result = getServerSeasonMethod.invoke(null);
            } else {
                result = getServerSeasonMethod.invoke(null, (Level) null);
            }
            RanchFestivalMod.LOGGER.debug("getServerSeason returned: {}", result);
            return result;
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error calling getServerSeason", e);
            return null;
        }
    }

    /**
     * 获取当前子季节
     * @return 当前的子季节，如果Serene Seasons未加载或API不可用则返回null
     */
    public static Object getCurrentSubSeason() {
        initializeReflection(); // 确保初始化
        
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
            RanchFestivalMod.LOGGER.debug("getCurrentSubSeason returned: {}", result);
            return result;
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error calling getCurrentSubSeason", e);
            return null;
        }
    }

    /**
     * 获取当前季节的第几天
     * @return 当前季节的第几天，如果Serene Seasons未加载或API不可用则返回-1
     */
    public static int getDayOfSeason() {
        initializeReflection(); // 确保初始化
        
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
            
            if (result instanceof Integer) {
                int day = (Integer) result;
                RanchFestivalMod.LOGGER.debug("getDayOfSeason returned: {}", day);
                return day;
            } else {
                RanchFestivalMod.LOGGER.warn("getDayOfSeason returned unexpected type: {}", result != null ? result.getClass() : "null");
                return -1;
            }
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error calling getDayOfSeason", e);
            return -1;
        }
    }

    /**
     * 获取当前季节
     * @return 当前的季节，如果Serene Seasons未加载或API不可用则返回null
     */
    public static Object getCurrentSeason() {
        initializeReflection(); // 确保初始化
        
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
            RanchFestivalMod.LOGGER.debug("getCurrentSeason returned: {}", result);
            return result;
        } catch (Exception e) {
            RanchFestivalMod.LOGGER.error("Error calling getCurrentSeason", e);
            return null;
        }
    }

    /**
     * 获取当前子季节的字符串表示
     * @return 当前子季节的字符串，如果无法获取则返回"UNKNOWN"
     */
    public static String getCurrentSubSeasonString() {
        Object subSeason = getCurrentSubSeason();
        if (subSeason != null) {
            return subSeason.toString();
        }
        return "UNKNOWN";
    }

    /**
     * 获取当前季节的字符串表示
     * @return 当前季节的字符串，如果无法获取则返回"UNKNOWN"
     */
    public static String getCurrentSeasonString() {
        Object season = getCurrentSeason();
        if (season != null) {
            return season.toString();
        }
        return "UNKNOWN";
    }
    
    /**
     * 检查API是否可用
     */
    public static boolean isApiAvailable() {
        initializeReflection();
        return apiAvailable;
    }
}