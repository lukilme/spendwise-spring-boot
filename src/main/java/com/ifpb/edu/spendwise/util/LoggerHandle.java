package com.ifpb.edu.spendwise.util;
// CÓDIGO ROUBADO DE ALGUM LUGAR, NÃO É MEU
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

@SuppressWarnings("unused")
public class LoggerHandle {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String ITALIC = "\u001B[3m";
    private static final String UNDERLINE = "\u001B[4m";

    private static final String BLINK = "\u001B[5m";
    private static final String REVERSE = "\u001B[7m";
    private static final String STRIKETHROUGH = "\u001B[9m";

    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    private static final String BRIGHT_BLACK = "\u001B[90m";
    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String BRIGHT_BLUE = "\u001B[94m";
    private static final String BRIGHT_MAGENTA = "\u001B[95m";
    private static final String BRIGHT_CYAN = "\u001B[96m";
    private static final String BRIGHT_WHITE = "\u001B[97m";

    private static final String BG_BLACK = "\u001B[40m";
    private static final String BG_RED = "\u001B[41m";
    private static final String BG_GREEN = "\u001B[42m";
    private static final String BG_YELLOW = "\u001B[43m";
    private static final String BG_BLUE = "\u001B[44m";
    private static final String BG_MAGENTA = "\u001B[45m";
    private static final String BG_CYAN = "\u001B[46m";
    private static final String BG_WHITE = "\u001B[47m";

    private static final String BG_BRIGHT_BLACK = "\u001B[100m";
    private static final String BG_BRIGHT_RED = "\u001B[101m";
    private static final String BG_BRIGHT_GREEN = "\u001B[102m";
    private static final String BG_BRIGHT_YELLOW = "\u001B[103m";
    private static final String BG_BRIGHT_BLUE = "\u001B[104m";
    private static final String BG_BRIGHT_MAGENTA = "\u001B[105m";
    private static final String BG_BRIGHT_CYAN = "\u001B[106m";
    private static final String BG_BRIGHT_WHITE = "\u001B[107m";

    private static final String KEY_STYLE = BOLD + UNDERLINE + BRIGHT_CYAN + BG_BLACK;
    private static final String STRING_VALUE_STYLE = ITALIC + BRIGHT_GREEN;
    private static final String NUMBER_VALUE_STYLE = BOLD + BRIGHT_YELLOW + BG_BLUE;
    private static final String BOOLEAN_TRUE_STYLE = BOLD + BRIGHT_GREEN + UNDERLINE;
    private static final String BOOLEAN_FALSE_STYLE = BOLD + BRIGHT_RED + STRIKETHROUGH;
    private static final String NULL_VALUE_STYLE = DIM + BRIGHT_BLACK + ITALIC;
    private static final String PUNCTUATION_STYLE = BRIGHT_WHITE + BOLD;
    private static final String ARRAY_STYLE = BRIGHT_MAGENTA + UNDERLINE;
    private static final String OBJECT_STYLE = BRIGHT_BLUE + BOLD;
    private static final String BORDER_STYLE = BOLD + BRIGHT_CYAN + BG_BLACK;

    public static void infoIterator(int index, String message) {
        System.out.println(BRIGHT_CYAN + index + ":" + message + RESET);
    }

    public static void erro(Exception exception) {
        StringBuilder builder = new StringBuilder();

        builder.append(BG_BRIGHT_RED)
                .append(BRIGHT_YELLOW)
                .append(BOLD)
                .append(UNDERLINE)
                .append(" EXCEÇÃO DETECTADA ")
                .append(RESET)
                .append("\n");

        builder.append(KEY_STYLE)
                .append("Tipo: ")
                .append(RESET)
                .append(STRING_VALUE_STYLE)
                .append(exception.getClass().getSimpleName())
                .append(RESET)
                .append("\n");

        builder.append(KEY_STYLE)
                .append("Mensagem: ")
                .append(RESET)
                .append(STRING_VALUE_STYLE)
                .append(exception.getMessage() != null ? exception.getMessage() : "Sem mensagem")
                .append(RESET)
                .append("\n");

        StackTraceElement[] trace = exception.getStackTrace();
        if (trace.length > 0) {
            StackTraceElement origin = trace[0];
            builder.append(KEY_STYLE)
                    .append("Origem: ")
                    .append(RESET)
                    .append(BRIGHT_WHITE)
                    .append(origin.getClassName())
                    .append(".")
                    .append(origin.getMethodName())
                    .append(" (linha ")
                    .append(origin.getLineNumber())
                    .append(")")
                    .append(RESET)
                    .append("\n");
        }

        System.out.println(builder.toString());
    }

    public static void warning(String message) {
        System.out.println(BG_BRIGHT_YELLOW + BLACK + BOLD + UNDERLINE + "WARNING:" + message + RESET);
    }

    public static void info(String message) {
        System.out.println(BRIGHT_GREEN + "INFO:" + message + RESET);
    }

    public static void infoWData(String message, Map<String, Object> data) {
        System.out.println(BRIGHT_GREEN + "INFO:" + message + RESET);
        System.out.println(formatToColorfulJson(data));
    }

    public static void warningWData(String message, Map<String, Object> data) {
        System.out.println(BRIGHT_MAGENTA + BOLD + UNDERLINE + "WARNING:" + message + RESET);
        System.out.println(formatToColorfulJson(data));
    }

    // ========================================================================================================//

    public static String formatToColorfulJson(Map<String, Object> logData) {
        return formatToColorfulJson(logData, true);
    }

    public static String formatToColorfulJson(Map<String, Object> logData, boolean withBorder) {
        StringBuilder sb = new StringBuilder();

        if (withBorder) {
            addBorder(sb, "╔═══════════════════════════════════════╗");
            addBorder(sb, "║              JSON LOG DATA            ║");
            addBorder(sb, "╚═══════════════════════════════════════╝");
            sb.append("\n");
        }

        sb.append(PUNCTUATION_STYLE).append("{").append(RESET).append("\n");

        int count = 0;
        for (Map.Entry<String, Object> entry : logData.entrySet()) {
            sb.append("  ")
                    .append(KEY_STYLE).append("\"").append(entry.getKey()).append("\"").append(RESET)
                    .append(PUNCTUATION_STYLE).append(": ").append(RESET);

            formatValue(sb, entry.getValue(), 1);

            if (++count < logData.size()) {
                sb.append(PUNCTUATION_STYLE).append(",").append(RESET);
            }
            sb.append("\n");
        }

        sb.append(PUNCTUATION_STYLE).append("}").append(RESET);

        if (withBorder) {
            sb.append("\n\n");
            addBorder(sb, "╔═══════════════════════════════════════╗");
            addBorder(sb, "║                 END LOG               ║");
            addBorder(sb, "╚═══════════════════════════════════════╝");
        }

        return sb.toString();
    }

    private static void formatValue(StringBuilder sb, Object value, int indentLevel) {
        String indent = "  ".repeat(indentLevel);

        if (value == null) {
            sb.append(NULL_VALUE_STYLE).append("null").append(RESET);
        } else if (value instanceof String) {
            sb.append(STRING_VALUE_STYLE).append("\"").append(escapeString((String) value)).append("\"").append(RESET);
        } else if (value instanceof Integer || value instanceof Long) {
            sb.append(NUMBER_VALUE_STYLE).append(" ").append(value).append(" ").append(RESET);
        } else if (value instanceof Double || value instanceof Float) {
            sb.append(NUMBER_VALUE_STYLE).append(" ").append(String.format("%.2f", value)).append(" ").append(RESET);
        } else if (value instanceof Boolean) {
            Boolean boolValue = (Boolean) value;
            if (boolValue) {
                sb.append(BOOLEAN_TRUE_STYLE).append("true").append(RESET);
            } else {
                sb.append(BOOLEAN_FALSE_STYLE).append("false").append(RESET);
            }
        } else if (value instanceof List || value instanceof Object[]) {
            formatArray(sb, value, indentLevel);
        } else if (value instanceof Map) {
            formatNestedObject(sb, (Map<?, ?>) value, indentLevel);
        } else {

            sb.append(OBJECT_STYLE).append("\"").append(escapeString(String.valueOf(value))).append("\"").append(RESET);
        }
    }

    private static void formatArray(StringBuilder sb, Object arrayValue, int indentLevel) {
        sb.append(ARRAY_STYLE).append("[").append(RESET);

        Collection<?> collection;
        if (arrayValue instanceof Object[]) {
            collection = List.of((Object[]) arrayValue);
        } else {
            collection = (Collection<?>) arrayValue;
        }

        if (!collection.isEmpty()) {
            sb.append("\n");
            int count = 0;
            for (Object item : collection) {
                sb.append("  ".repeat(indentLevel + 1));
                formatValue(sb, item, indentLevel + 1);
                if (++count < collection.size()) {
                    sb.append(PUNCTUATION_STYLE).append(",").append(RESET);
                }
                sb.append("\n");
            }
            sb.append("  ".repeat(indentLevel));
        }

        sb.append(ARRAY_STYLE).append("]").append(RESET);
    }

    private static void formatNestedObject(StringBuilder sb, Map<?, ?> map, int indentLevel) {
        sb.append(OBJECT_STYLE).append("{").append(RESET);

        if (!map.isEmpty()) {
            sb.append("\n");
            int count = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                sb.append("  ".repeat(indentLevel + 1))
                        .append(KEY_STYLE).append("\"").append(entry.getKey()).append("\"").append(RESET)
                        .append(PUNCTUATION_STYLE).append(": ").append(RESET);

                formatValue(sb, entry.getValue(), indentLevel + 1);

                if (++count < map.size()) {
                    sb.append(PUNCTUATION_STYLE).append(",").append(RESET);
                }
                sb.append("\n");
            }
            sb.append("  ".repeat(indentLevel));
        }

        sb.append(OBJECT_STYLE).append("}").append(RESET);
    }

    private static void addBorder(StringBuilder sb, String text) {
        sb.append(BORDER_STYLE).append(text).append(RESET).append("\n");
    }

    private static String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static void logColorful(String message, Map<String, Object> data) {
        System.out.println(BRIGHT_MAGENTA + BOLD + "🚀 " + message + RESET);
        System.out.println(formatToColorfulJson(data));
    }

    public static void logWithTimestamp(Map<String, Object> data) {
        String timestamp = java.time.LocalDateTime.now().toString();
        System.out.println(BRIGHT_YELLOW + BOLD + " Timestamp: " + timestamp + RESET);
        System.out.println(formatToColorfulJson(data));
    }

    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> clazz = obj.getClass();

        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                try {
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                            field.isSynthetic()) {
                        continue;
                    }

                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (value != null && !isPrimitiveOrWrapper(value) &&
                            !(value instanceof String) && !(value instanceof Collection) &&
                            !(value instanceof Map) && !value.getClass().isArray()) {
                        value = objectToMap(value);
                    }

                    map.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    map.put(field.getName(), "Acesso negado: " + e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }

        return map;
    }

    private static boolean isPrimitiveOrWrapper(Object obj) {
        return obj instanceof Number ||
                obj instanceof Boolean ||
                obj instanceof Character ||
                obj.getClass().isPrimitive();
    }

    public static Map<String, Object> objectToMapViaGetters(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        Method[] methods = obj.getClass().getMethods();

        for (Method method : methods) {
            String methodName = method.getName();

            if (isValidGetter(method)) {
                try {
                    Object value = method.invoke(obj);
                    String propertyName = getPropertyNameFromGetter(methodName);

                    if (value != null && !isPrimitiveOrWrapper(value) &&
                            !(value instanceof String) && !(value instanceof Collection) &&
                            !(value instanceof Map) && !value.getClass().isArray()) {
                        value = objectToMapViaGetters(value);
                    }

                    map.put(propertyName, value);
                } catch (Exception e) {
                    map.put(getPropertyNameFromGetter(methodName),
                            "Erro ao acessar: " + e.getMessage());
                }
            }
        }

        return map;
    }

    public static Map<String, Object> objectToMapHybrid(Object obj) {
        Map<String, Object> getterMap = objectToMapViaGetters(obj);

        if (getterMap.isEmpty()) {
            return objectToMap(obj);
        }

        Map<String, Object> fieldMap = objectToMap(obj);
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            getterMap.putIfAbsent(entry.getKey(), entry.getValue());
        }

        return getterMap;
    }

    private static boolean isValidGetter(Method method) {
        String name = method.getName();
        return (name.startsWith("get") && name.length() > 3 &&
                method.getParameterCount() == 0 &&
                !method.getReturnType().equals(void.class) &&
                !name.equals("getClass")) ||
                (name.startsWith("is") && name.length() > 2 &&
                        method.getParameterCount() == 0 &&
                        (method.getReturnType().equals(boolean.class) ||
                                method.getReturnType().equals(Boolean.class)));
    }

    private static String getPropertyNameFromGetter(String getterName) {
        if (getterName.startsWith("get")) {
            String property = getterName.substring(3);
            return Character.toLowerCase(property.charAt(0)) + property.substring(1);
        } else if (getterName.startsWith("is")) {
            String property = getterName.substring(2);
            return Character.toLowerCase(property.charAt(0)) + property.substring(1);
        }
        return getterName;
    }

}