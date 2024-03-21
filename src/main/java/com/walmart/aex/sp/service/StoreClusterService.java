package com.walmart.aex.sp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StoreClusterService {

    public Map<String, List<Integer>> fetchPOStoreClusterGrouping(String season, String fiscalYear) {
        log.info("Fetching PO Store Cluster Grouping from StoreClusterAPI...");

        // TODO - Replace with Store Cluster API call
        Map<String, List<Integer>> storeGrouping = new HashMap<>();
        storeGrouping.put("offshore", List.of(1822, 1854, 2026, 2067, 2070, 2071, 2072, 2074, 2085, 2126, 2188, 2240, 2302, 2308, 2314, 2321, 2346, 2423, 2449, 2473, 2501, 2710, 2711, 2721, 2722, 3149, 3290, 3478, 3693, 3716, 3883, 4359, 4474, 5274, 5793, 5802, 5803));
        storeGrouping.put("onshore", List.of(1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 188, 189, 190, 192, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 209, 210, 211, 212, 213, 214, 216, 217, 218, 219, 220, 221, 222, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 236, 237, 238, 239, 240, 242, 243, 244, 246, 247, 248, 249, 250, 251, 252, 253, 254, 256, 257, 258, 259, 260, 261, 262, 264, 265, 266, 267, 268, 269, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346, 347, 348, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 368, 369, 370, 371, 372, 373, 374, 375, 376, 378, 379, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 399, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 430, 431, 432, 433, 434, 435, 436, 437, 438, 440, 442, 443, 444, 445, 446, 447, 448, 449, 450, 451, 452, 453, 454, 456, 457, 458, 459, 461, 462, 463, 464, 465, 466, 467, 468, 469, 470, 471, 472, 473, 475, 476, 477, 478, 479, 480, 481, 482, 483, 484, 485, 486, 488, 489, 490, 491, 492, 493, 494, 495, 497, 498, 499, 500, 501, 502, 503, 504, 505, 506, 507, 508, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521, 522, 523, 524, 526, 527, 528, 529, 530, 531, 532, 533, 535, 536, 537, 538, 539, 540, 541, 542, 543, 544, 545, 546, 547, 548, 549, 550, 551, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 571, 572, 573, 574, 575, 576, 577, 578, 579, 580, 581, 582, 583, 584, 585, 586, 587, 588, 589, 590, 591, 592, 593, 594, 595, 596, 598, 599, 600, 601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 648, 649, 651, 652, 653, 655, 656, 657, 658, 659, 660, 661, 662, 663, 664, 665, 666, 667, 668, 669, 670, 671, 672, 673, 674, 675, 676, 677, 678, 680, 681, 682, 683, 684, 685, 686, 687, 688, 689, 690, 691, 692, 693, 694, 695, 696, 697, 698, 699, 700, 701, 702, 703, 705, 706, 707, 708, 709, 710, 711, 712, 713, 714, 715, 716, 718, 719, 720, 721, 722, 723, 724, 725, 726, 727, 728, 729, 730, 731, 733, 734, 735, 736, 737, 738, 739, 740, 741, 742, 743, 744, 745, 746, 747, 748, 749, 750, 751, 752, 753, 754, 755, 756, 757, 758, 759, 760, 761, 762, 764, 765, 766, 767, 768, 769, 770, 771, 772, 773, 774, 775, 776, 777, 778, 779, 780, 781, 782, 783, 784, 785, 786, 787, 788, 789, 790, 791, 792, 793, 794, 795, 796, 797, 798, 799, 800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811, 812, 813, 814, 815, 816, 817, 818, 819, 820, 821, 822, 823, 824, 825, 826, 827, 828, 829, 830, 831, 833, 834, 835, 836, 837, 838, 839, 841, 842, 843, 844, 845, 846, 847, 848, 849, 850, 851, 852, 853, 854, 855, 856, 857, 858, 859, 860, 861, 862, 863, 864, 865, 866, 867, 868, 869, 870, 871, 872, 873, 874, 875, 876, 877, 878, 879, 880, 881, 882, 884, 885, 886, 887, 888, 889, 890, 891, 892, 893, 894, 895, 896, 897, 898, 899, 900, 901, 902, 903, 904, 905, 906, 907, 908, 909, 910, 911, 912, 913, 914, 915, 916, 917, 918, 919, 921, 922, 923, 924, 925, 927, 928, 929, 930, 931, 932, 933, 934, 935, 936, 937, 938, 939, 940, 941, 942, 943, 944, 945, 947, 948, 950, 951, 952, 953, 954, 955, 956, 957, 958, 959, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975, 976, 977, 978, 979, 980, 981, 982, 984, 985, 986, 987, 988, 989, 990, 991, 992, 993, 994, 995, 996, 999, 1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1020, 1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1029, 1030, 1032, 1033, 1034, 1035, 1036, 1037, 1038, 1039, 1040, 1041, 1042, 1043, 1044, 1045, 1046, 1047, 1048, 1051, 1052, 1053, 1054, 1055, 1056, 1057, 1058, 1059, 1060, 1061, 1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1071, 1072, 1073, 1074, 1075, 1076, 1077, 1078, 1079, 1080, 1081, 1082, 1083, 1084, 1085, 1086, 1087, 1088, 1089, 1090, 1091, 1093, 1094, 1095, 1096, 1097, 1098, 1099, 1100, 1101, 1102, 1103, 1104, 1105, 1106, 1107, 1108, 1109, 1110, 1111, 1112, 1113, 1114, 1115, 1116, 1117, 1118, 1119, 1120, 1121, 1122, 1123, 1124, 1125, 1126, 1128, 1129, 1130, 1131, 1132, 1133, 1134, 1135, 1136, 1137, 1138, 1139, 1140, 1141, 1142, 1143, 1144, 1146, 1147, 1148, 1149, 1150, 1151, 1152, 1153, 1154, 1155, 1156, 1157, 1158, 1159, 1160, 1161, 1162, 1163, 1164, 1165, 1166, 1167, 1168, 1169, 1170, 1171, 1172, 1173, 1174, 1175, 1176, 1177, 1178, 1179, 1180, 1181, 1182, 1183, 1184, 1185, 1186, 1187, 1188, 1189, 1190, 1191, 1192, 1193, 1194, 1195, 1196, 1197, 1198, 1199, 1200, 1201, 1202, 1203, 1204, 1205, 1206, 1207, 1209, 1210, 1211, 1212, 1213, 1214, 1215, 1216, 1217, 1218, 1219, 1220, 1221, 1222, 1223, 1224, 1225, 1226, 1227, 1228, 1229, 1230, 1231, 1232, 1233, 1234, 1235, 1236, 1237, 1238, 1239, 1240, 1241, 1242, 1243, 1244, 1245, 1247, 1248, 1249, 1252, 1253, 1254, 1255, 1256, 1257, 1259, 1260, 1261, 1263, 1264, 1265, 1266, 1267, 1268, 1270, 1271, 1272, 1273, 1274, 1275, 1276, 1277, 1279, 1280, 1281, 1282, 1283, 1284, 1285, 1286, 1287, 1288, 1289, 1290, 1291, 1292, 1293, 1294, 1295, 1296, 1297, 1298, 1299, 1300, 1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308, 1309, 1310, 1311, 1312, 1313, 1314, 1315, 1316, 1317, 1318, 1319, 1320, 1321, 1322, 1323, 1324, 1325, 1326, 1327, 1328, 1329, 1330, 1331, 1332, 1333, 1334, 1335, 1336, 1337, 1338, 1339, 1340, 1341, 1342, 1344, 1345, 1346, 1347, 1348, 1349, 1350, 1351, 1352, 1353, 1354, 1355, 1356, 1357, 1358, 1359, 1360, 1361, 1362, 1363, 1364, 1365, 1366, 1367, 1368, 1369, 1370, 1371, 1372, 1373, 1374, 1375, 1376, 1377, 1378, 1379, 1380, 1381, 1382, 1383, 1384, 1385, 1386, 1387, 1388, 1389, 1390, 1391, 1392, 1393, 1394, 1395, 1396, 1397, 1398, 1399, 1400, 1401, 1403, 1404, 1405, 1406, 1407, 1408, 1409, 1410, 1411, 1412, 1413, 1414, 1415, 1416, 1417, 1418, 1419, 1420, 1421, 1422, 1423, 1424, 1425, 1426, 1427, 1428, 1429, 1430, 1431, 1432, 1433, 1434, 1435, 1436, 1437, 1438, 1439, 1440, 1441, 1442, 1443, 1444, 1445, 1446, 1447, 1448, 1449, 1450, 1451, 1452, 1453, 1454, 1455, 1456, 1457, 1458, 1459, 1460, 1461, 1462, 1463, 1464, 1465, 1466, 1467, 1468, 1469, 1470, 1471, 1472, 1473, 1474, 1475, 1476, 1477, 1478, 1479, 1480, 1481, 1482, 1483, 1485, 1486, 1487, 1488, 1489, 1490, 1491, 1492, 1493, 1494, 1495, 1496, 1497, 1498, 1499, 1500, 1501, 1502, 1503, 1504, 1505, 1506, 1507, 1508, 1509, 1510, 1511, 1512, 1513, 1514, 1515, 1516, 1517, 1518, 1519, 1520, 1521, 1522, 1523, 1524, 1525, 1526, 1527, 1528, 1529, 1530, 1531, 1532, 1533, 1534, 1535, 1536, 1537, 1538, 1539, 1540, 1541, 1542, 1543, 1544, 1545, 1546, 1547, 1548, 1549, 1550, 1551, 1552, 1553, 1554, 1555, 1556, 1557, 1558, 1559, 1560, 1561, 1562, 1563, 1564, 1565, 1566, 1567, 1568, 1569, 1570, 1571, 1572, 1573, 1574, 1575, 1576, 1577, 1578, 1579, 1580, 1581, 1583, 1584, 1585, 1586, 1587, 1588, 1589, 1590, 1591, 1592, 1593, 1594, 1595, 1596, 1597, 1598, 1599, 1600, 1601, 1602, 1603, 1604, 1605, 1606, 1607, 1608, 1609, 1610, 1611, 1612, 1613, 1614, 1615, 1616, 1617, 1618, 1619, 1620, 1621, 1622, 1623, 1624, 1625, 1626, 1627, 1628, 1629, 1630, 1631, 1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641, 1642, 1643, 1644, 1645, 1646, 1647, 1648, 1649, 1650, 1651, 1652, 1653, 1654, 1655, 1656, 1657, 1658, 1659, 1660, 1661, 1662, 1663, 1664, 1665, 1666, 1667, 1668, 1669, 1670, 1671, 1672, 1673, 1674, 1675, 1676, 1677, 1678, 1679, 1680, 1681, 1682, 1683, 1684, 1685, 1686, 1687, 1688, 1689, 1690, 1691, 1692, 1693, 1694, 1695, 1696, 1697, 1698, 1699, 1700, 1701, 1702, 1703, 1704, 1705, 1707, 1708, 1709, 1710, 1711, 1712, 1713, 1714, 1715, 1716, 1717, 1718, 1719, 1720, 1721, 1722, 1723, 1724, 1726, 1727, 1728, 1729, 1730, 1731, 1732, 1733, 1734, 1735, 1736, 1737, 1738, 1739, 1740, 1741, 1742, 1743, 1744, 1745, 1746, 1747, 1748, 1749, 1750, 1751, 1752, 1753, 1754, 1755, 1756, 1757, 1758, 1759, 1760, 1761, 1762, 1763, 1764, 1765, 1766, 1767, 1768, 1769, 1770, 1771, 1772, 1773, 1774, 1775, 1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785, 1786, 1787, 1788, 1789, 1790, 1791, 1792, 1793, 1794, 1795, 1796, 1797, 1798, 1799, 1800, 1801, 1802, 1803, 1804, 1805, 1806, 1807, 1808, 1809, 1810, 1811, 1812, 1813, 1814, 1815, 1816, 1817, 1819, 1820, 1821, 1823, 1825, 1826, 1827, 1828, 1829, 1830, 1831, 1832, 1833, 1834, 1835, 1836, 1837, 1838, 1839, 1840, 1841, 1842, 1843, 1844, 1845, 1846, 1847, 1848, 1849, 1850, 1851, 1852, 1853, 1855, 1856, 1857, 1858, 1859, 1860, 1861, 1862, 1863, 1864, 1865, 1866, 1867, 1868, 1869, 1870, 1871, 1872, 1873, 1874, 1875, 1876, 1877, 1878, 1879, 1880, 1881, 1882, 1883, 1884, 1885, 1886, 1887, 1888, 1889, 1890, 1891, 1892, 1893, 1894, 1895, 1896, 1897, 1898, 1899, 1900, 1901, 1902, 1903, 1904, 1905, 1906, 1907, 1908, 1909, 1910, 1911, 1912, 1913, 1914, 1915, 1916, 1917, 1918, 1919, 1920, 1921, 1922, 1923, 1924, 1925, 1926, 1927, 1928, 1929, 1930, 1931, 1932, 1933, 1934, 1935, 1936, 1937, 1938, 1939, 1940, 1941, 1942, 1943, 1945, 1946, 1947, 1948, 1949, 1950, 1951, 1952, 1953, 1954, 1955, 1956, 1957, 1958, 1959, 1960, 1961, 1962, 1963, 1964, 1965, 1966, 1967, 1968, 1969, 1970, 1971, 1972, 1973, 1974, 1975, 1976, 1977, 1978, 1979, 1980, 1981, 1982, 1983, 1984, 1985, 1986, 1987, 1988, 1989, 1990, 1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2012, 2013, 2014, 2015, 2016, 2019, 2020, 2021, 2022, 2023, 2024, 2025, 2027, 2028, 2030, 2031, 2032, 2033, 2035, 2036, 2037, 2038, 2039, 2040, 2041, 2043, 2044, 2045, 2046, 2047, 2048, 2049, 2050, 2051, 2052, 2053, 2054, 2055, 2056, 2057, 2058, 2059, 2061, 2062, 2063, 2064, 2065, 2066, 2068, 2069, 2073, 2075, 2077, 2078, 2079, 2080, 2081, 2082, 2083, 2084, 2086, 2087, 2088, 2089, 2090, 2091, 2092, 2093, 2094, 2095, 2096, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2111, 2112, 2113, 2114, 2115, 2116, 2117, 2118, 2119, 2120, 2121, 2122, 2123, 2124, 2125, 2127, 2128, 2129, 2130, 2131, 2132, 2133, 2134, 2135, 2136, 2137, 2138, 2139, 2140, 2141, 2142, 2143, 2144, 2145, 2146, 2147, 2149, 2150, 2151, 2152, 2153, 2154, 2155, 2156, 2157, 2158, 2159, 2160, 2161, 2163, 2164, 2166, 2167, 2169, 2170, 2174, 2175, 2176, 2177, 2178, 2180, 2181, 2183, 2184, 2185, 2187, 2189, 2190, 2191, 2192, 2193, 2194, 2195, 2196, 2197, 2198, 2199, 2200, 2201, 2202, 2204, 2205, 2206, 2207, 2208, 2209, 2210, 2211, 2213, 2214, 2215, 2218, 2221, 2222, 2223, 2224, 2225, 2227, 2228, 2230, 2231, 2232, 2233, 2234, 2236, 2237, 2238, 2239, 2241, 2242, 2243, 2244, 2245, 2247, 2248, 2249, 2250, 2251, 2252, 2253, 2254, 2255, 2256, 2257, 2258, 2259, 2261, 2262, 2263, 2264, 2265, 2266, 2267, 2269, 2270, 2271, 2272, 2273, 2274, 2275, 2276, 2277, 2278, 2279, 2280, 2281, 2282, 2283, 2284, 2285, 2286, 2287, 2288, 2289, 2291, 2292, 2293, 2294, 2295, 2297, 2299, 2300, 2306, 2307, 2309, 2310, 2311, 2312, 2313, 2315, 2316, 2317, 2318, 2319, 2320, 2322, 2323, 2325, 2326, 2329, 2330, 2331, 2332, 2333, 2334, 2335, 2336, 2337, 2338, 2339, 2340, 2341, 2348, 2350, 2351, 2352, 2354, 2355, 2357, 2358, 2359, 2360, 2361, 2362, 2365, 2366, 2367, 2368, 2369, 2370, 2371, 2373, 2385, 2386, 2387, 2388, 2398, 2399, 2400, 2401, 2402, 2403, 2404, 2405, 2412, 2417, 2418, 2419, 2420, 2421, 2422, 2424, 2426, 2427, 2428, 2429, 2432, 2434, 2435, 2436, 2437, 2438, 2439, 2440, 2441, 2443, 2444, 2445, 2446, 2447, 2448, 2450, 2452, 2453, 2455, 2457, 2458, 2459, 2460, 2463, 2464, 2469, 2471, 2472, 2474, 2475, 2476, 2477, 2479, 2480, 2481, 2482, 2483, 2484, 2485, 2490, 2491, 2492, 2493, 2494, 2495, 2496, 2497, 2502, 2503, 2504, 2505, 2506, 2507, 2508, 2509, 2510, 2511, 2512, 2513, 2514, 2515, 2516, 2517, 2518, 2519, 2520, 2522, 2523, 2526, 2527, 2528, 2529, 2530, 2531, 2532, 2533, 2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543, 2544, 2545, 2546, 2547, 2548, 2549, 2550, 2551, 2552, 2553, 2554, 2555, 2556, 2557, 2558, 2559, 2560, 2561, 2562, 2563, 2564, 2565, 2566, 2567, 2568, 2571, 2572, 2574, 2575, 2576, 2577, 2579, 2580, 2581, 2582, 2583, 2584, 2585, 2586, 2587, 2588, 2591, 2592, 2593, 2594, 2595, 2596, 2597, 2598, 2599, 2600, 2603, 2604, 2605, 2607, 2608, 2609, 2610, 2611, 2612, 2613, 2614, 2615, 2616, 2617, 2618, 2619, 2620, 2621, 2626, 2627, 2628, 2629, 2630, 2631, 2633, 2634, 2636, 2637, 2638, 2639, 2640, 2641, 2642, 2643, 2644, 2645, 2646, 2647, 2648, 2649, 2650, 2651, 2652, 2653, 2654, 2656, 2658, 2659, 2660, 2662, 2663, 2664, 2665, 2666, 2667, 2668, 2671, 2672, 2674, 2678, 2679, 2680, 2681, 2682, 2683, 2684, 2687, 2688, 2690, 2691, 2692, 2693, 2694, 2695, 2696, 2697, 2700, 2702, 2703, 2704, 2705, 2706, 2708, 2712, 2713, 2714, 2715, 2716, 2717, 2718, 2719, 2720, 2723, 2724, 2725, 2726, 2727, 2728, 2729, 2730, 2732, 2733, 2734, 2735, 2739, 2740, 2747, 2748, 2749, 2751, 2752, 2753, 2754, 2755, 2756, 2757, 2758, 2760, 2761, 2762, 2763, 2764, 2765, 2766, 2767, 2768, 2769, 2771, 2772, 2774, 2777, 2778, 2780, 2781, 2782, 2783, 2784, 2785, 2786, 2787, 2788, 2789, 2790, 2791, 2792, 2793, 2794, 2795, 2796, 2797, 2799, 2803, 2804, 2805, 2806, 2807, 2808, 2809, 2810, 2811, 2812, 2813, 2814, 2815, 2816, 2817, 2818, 2819, 2820, 2821, 2822, 2823, 2825, 2827, 2828, 2830, 2831, 2832, 2833, 2834, 2836, 2838, 2841, 2842, 2843, 2844, 2845, 2846, 2847, 2849, 2850, 2852, 2853, 2854, 2855, 2856, 2857, 2859, 2860, 2861, 2862, 2864, 2865, 2866, 2867, 2869, 2871, 2872, 2873, 2874, 2881, 2882, 2883, 2884, 2885, 2886, 2888, 2889, 2890, 2891, 2892, 2893, 2894, 2897, 2898, 2899, 2900, 2901, 2902, 2903, 2904, 2905, 2906, 2908, 2909, 2910, 2911, 2912, 2913, 2914, 2915, 2916, 2917, 2918, 2920, 2921, 2922, 2923, 2924, 2925, 2926, 2927, 2928, 2929, 2931, 2932, 2933, 2934, 2935, 2936, 2937, 2938, 2939, 2941, 2945, 2946, 2947, 2948, 2950, 2951, 2952, 2953, 2954, 2955, 2956, 2957, 2958, 2959, 2962, 2963, 2964, 2965, 2966, 2967, 2968, 2973, 2978, 2980, 2985, 2986, 2987, 2988, 2989, 2990, 2991, 2992, 2993, 2994, 2996, 3004, 3008, 3014, 3017, 3018, 3035, 3044, 3053, 3055, 3056, 3057, 3058, 3059, 3060, 3061, 3067, 3075, 3077, 3078, 3081, 3087, 3088, 3093, 3102, 3103, 3106, 3107, 3112, 3114, 3118, 3119, 3136, 3137, 3150, 3151, 3159, 3163, 3167, 3169, 3170, 3177, 3180, 3182, 3188, 3191, 3192, 3197, 3200, 3201, 3205, 3206, 3207, 3208, 3209, 3210, 3212, 3213, 3214, 3215, 3216, 3217, 3218, 3219, 3220, 3221, 3222, 3223, 3224, 3225, 3226, 3227, 3228, 3229, 3230, 3231, 3232, 3233, 3234, 3235, 3236, 3237, 3239, 3241, 3243, 3245, 3247, 3248, 3250, 3251, 3252, 3253, 3254, 3255, 3258, 3259, 3260, 3261, 3262, 3265, 3266, 3267, 3268, 3269, 3270, 3271, 3273, 3274, 3276, 3277, 3278, 3279, 3280, 3281, 3282, 3283, 3284, 3285, 3286, 3288, 3289, 3291, 3292, 3293, 3294, 3295, 3296, 3297, 3298, 3300, 3301, 3302, 3303, 3304, 3305, 3307, 3308, 3309, 3310, 3311, 3313, 3319, 3320, 3322, 3324, 3328, 3329, 3331, 3332, 3334, 3336, 3337, 3339, 3342, 3344, 3347, 3348, 3349, 3350, 3351, 3352, 3360, 3362, 3363, 3364, 3366, 3367, 3370, 3371, 3372, 3379, 3380, 3381, 3382, 3383, 3384, 3386, 3387, 3388, 3389, 3390, 3391, 3394, 3395, 3397, 3400, 3401, 3402, 3403, 3404, 3406, 3407, 3408, 3409, 3412, 3415, 3417, 3418, 3420, 3422, 3423, 3425, 3427, 3428, 3429, 3430, 3431, 3432, 3433, 3434, 3435, 3436, 3439, 3443, 3445, 3447, 3453, 3454, 3455, 3458, 3459, 3460, 3461, 3462, 3463, 3464, 3465, 3469, 3471, 3472, 3473, 3474, 3475, 3476, 3477, 3480, 3481, 3482, 3483, 3484, 3485, 3486, 3487, 3488, 3489, 3490, 3491, 3492, 3493, 3494, 3495, 3497, 3498, 3499, 3500, 3501, 3502, 3503, 3505, 3510, 3511, 3513, 3514, 3515, 3516, 3518, 3520, 3522, 3523, 3524, 3525, 3526, 3527, 3528, 3529, 3531, 3533, 3534, 3535, 3537, 3538, 3541, 3543, 3544, 3545, 3546, 3547, 3548, 3549, 3560, 3561, 3562, 3563, 3564, 3566, 3567, 3568, 3569, 3570, 3571, 3572, 3573, 3580, 3581, 3582, 3583, 3584, 3585, 3587, 3588, 3589, 3590, 3591, 3595, 3596, 3597, 3598, 3601, 3602, 3607, 3608, 3609, 3611, 3612, 3616, 3620, 3621, 3623, 3624, 3625, 3626, 3627, 3630, 3631, 3632, 3633, 3634, 3639, 3640, 3641, 3643, 3644, 3645, 3648, 3650, 3652, 3658, 3659, 3660, 3700, 3701, 3702, 3705, 3708, 3709, 3710, 3712, 3717, 3720, 3722, 3725, 3726, 3728, 3729, 3730, 3731, 3732, 3733, 3734, 3738, 3739, 3741, 3742, 3747, 3748, 3749, 3750, 3751, 3754, 3757, 3758, 3760, 3761, 3762, 3763, 3764, 3765, 3768, 3770, 3771, 3772, 3773, 3774, 3775, 3777, 3778, 3780, 3781, 3782, 3783, 3784, 3785, 3786, 3789, 3790, 3791, 3792, 3794, 3795, 3796, 3799, 3801, 3802, 3803, 3804, 3805, 3806, 3807, 3809, 3810, 3812, 3823, 3824, 3825, 3826, 3827, 3828, 3829, 3830, 3831, 3833, 3835, 3837, 3838, 3840, 3842, 3843, 3847, 3848, 3850, 3851, 3852, 3853, 3857, 3858, 3860, 3861, 3864, 3867, 3868, 3869, 3872, 3874, 3875, 3876, 3877, 3884, 3886, 3887, 3888, 3889, 3891, 3893, 3894, 3896, 3897, 3912, 3947, 4049, 4055, 4063, 4064, 4065, 4068, 4101, 4102, 4103, 4108, 4111, 4112, 4115, 4118, 4129, 4130, 4131, 4132, 4137, 4144, 4153, 4156, 4161, 4162, 4163, 4164, 4165, 4166, 4168, 4171, 4176, 4183, 4185, 4187, 4191, 4194, 4195, 4200, 4201, 4202, 4203, 4208, 4209, 4215, 4216, 4219, 4221, 4223, 4224, 4226, 4230, 4231, 4232, 4235, 4237, 4238, 4239, 4240, 4241, 4243, 4244, 4246, 4247, 4249, 4250, 4252, 4253, 4255, 4256, 4257, 4258, 4259, 4260, 4262, 4272, 4273, 4274, 4276, 4277, 4278, 4279, 4281, 4283, 4284, 4285, 4286, 4288, 4295, 4298, 4299, 4303, 4309, 4317, 4318, 4321, 4322, 4323, 4325, 4330, 4332, 4333, 4334, 4335, 4336, 4337, 4340, 4341, 4348, 4350, 4352, 4355, 4356, 4358, 4365, 4368, 4370, 4372, 4374, 4377, 4378, 4381, 4383, 4384, 4387, 4389, 4392, 4393, 4394, 4395, 4399, 4403, 4404, 4405, 4407, 4409, 4410, 4412, 4414, 4416, 4417, 4420, 4423, 4424, 4427, 4428, 4430, 4435, 4436, 4438, 4439, 4440, 4444, 4446, 4450, 4451, 4456, 4457, 4458, 4459, 4460, 4461, 4471, 4472, 4475, 4476, 4477, 4479, 4482, 4483, 4484, 4487, 4488, 4497, 4499, 4501, 4503, 4506, 4508, 4509, 4511, 4512, 4514, 4520, 4521, 4526, 4529, 4531, 4533, 4538, 4540, 4543, 4553, 4554, 4556, 4557, 4558, 4564, 4567, 4568, 4569, 4570, 4574, 4581, 4583, 4588, 4590, 4593, 4594, 4597, 4599, 4600, 4601, 4606, 4609, 4611, 4612, 4615, 4616, 4623, 4627, 4631, 4635, 4636, 4637, 4638, 4639, 4641, 4643, 4644, 4653, 4660, 4664, 4677, 4678, 4679, 4681, 4682, 4683, 4687, 4689, 4690, 4695, 4696, 4697, 4699, 4734, 4849, 4865, 5003, 5012, 5022, 5023, 5025, 5027, 5028, 5029, 5030, 5031, 5032, 5033, 5034, 5035, 5036, 5037, 5038, 5039, 5040, 5041, 5044, 5045, 5046, 5047, 5048, 5049, 5051, 5054, 5055, 5056, 5057, 5058, 5060, 5063, 5064, 5065, 5066, 5070, 5071, 5072, 5073, 5075, 5076, 5077, 5078, 5079, 5080, 5082, 5083, 5085, 5087, 5088, 5089, 5090, 5091, 5092, 5095, 5096, 5097, 5099, 5101, 5103, 5104, 5105, 5111, 5113, 5115, 5116, 5117, 5118, 5123, 5124, 5126, 5127, 5129, 5130, 5131, 5133, 5134, 5136, 5137, 5139, 5140, 5141, 5142, 5144, 5145, 5146, 5147, 5150, 5151, 5152, 5154, 5155, 5156, 5157, 5159, 5160, 5162, 5165, 5166, 5167, 5168, 5170, 5172, 5173, 5174, 5175, 5178, 5184, 5185, 5186, 5189, 5190, 5191, 5192, 5193, 5195, 5196, 5197, 5199, 5200, 5201, 5202, 5203, 5210, 5211, 5214, 5215, 5216, 5218, 5219, 5220, 5227, 5228, 5229, 5231, 5232, 5233, 5234, 5235, 5236, 5239, 5240, 5241, 5242, 5244, 5245, 5246, 5247, 5250, 5251, 5252, 5253, 5254, 5260, 5261, 5262, 5263, 5264, 5266, 5270, 5272, 5273, 5275, 5276, 5278, 5280, 5281, 5282, 5285, 5287, 5288, 5292, 5293, 5294, 5295, 5296, 5298, 5299, 5300, 5301, 5303, 5305, 5307, 5309, 5311, 5312, 5313, 5316, 5317, 5319, 5320, 5325, 5326, 5329, 5330, 5331, 5334, 5335, 5337, 5338, 5339, 5340, 5341, 5342, 5343, 5344, 5345, 5346, 5347, 5348, 5350, 5352, 5355, 5356, 5358, 5359, 5360, 5361, 5363, 5368, 5369, 5370, 5373, 5374, 5376, 5379, 5381, 5382, 5384, 5385, 5386, 5387, 5388, 5389, 5390, 5391, 5392, 5394, 5396, 5397, 5398, 5399, 5402, 5403, 5404, 5409, 5410, 5411, 5414, 5416, 5417, 5418, 5419, 5420, 5422, 5424, 5425, 5426, 5428, 5429, 5430, 5432, 5433, 5434, 5435, 5436, 5438, 5439, 5440, 5441, 5442, 5443, 5444, 5445, 5446, 5447, 5448, 5449, 5450, 5455, 5458, 5460, 5461, 5462, 5463, 5466, 5469, 5470, 5471, 5472, 5476, 5477, 5479, 5480, 5481, 5482, 5483, 5485, 5486, 5487, 5488, 5489, 5491, 5492, 5494, 5495, 5497, 5498, 5499, 5612, 5616, 5617, 5625, 5626, 5628, 5629, 5632, 5641, 5644, 5649, 5667, 5668, 5669, 5671, 5672, 5673, 5676, 5686, 5701, 5703, 5705, 5706, 5707, 5709, 5710, 5713, 5717, 5727, 5728, 5731, 5732, 5735, 5743, 5745, 5747, 5748, 5751, 5752, 5753, 5761, 5763, 5764, 5766, 5768, 5772, 5777, 5779, 5781, 5786, 5787, 5795, 5797, 5799, 5806, 5807, 5808, 5809, 5810, 5812, 5814, 5823, 5835, 5839, 5841, 5842, 5843, 5844, 5845, 5847, 5848, 5851, 5853, 5854, 5857, 5858, 5859, 5861, 5864, 5866, 5867, 5869, 5871, 5872, 5874, 5879, 5880, 5881, 5882, 5883, 5884, 5886, 5888, 5889, 5890, 5891, 5893, 5894, 5898, 5899, 5911, 5912, 5922, 5927, 5929, 5930, 5932, 5935, 5936, 5938, 5941, 5944, 5947, 5954, 5956, 5957, 5959, 5964, 5965, 5968, 5969, 5976, 5977, 5987, 5992, 5996, 6173, 6190, 6286, 6300, 6370, 6392, 6394, 6395, 6397, 6467, 6469, 6476, 6477, 6480, 6576, 6579, 6789, 6807, 6878, 6879, 6887, 6898, 6929, 6931, 6932, 6951, 6952, 6958, 6963, 6964, 6978, 6998, 7009, 7020, 7032, 7087, 7129, 7156, 7178, 7179, 7181, 7185, 7188, 7194, 7199, 7238, 7243, 7245, 7246, 7247, 7249, 7280, 7281, 7290, 7294, 7301, 7324, 7326, 8331, 8930));

        return storeGrouping;

    }

}