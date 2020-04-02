package com.hprof.bitmap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.tools.perflib.captures.DataBuffer;
import com.android.tools.perflib.captures.MemoryMappedFileBuffer;
import com.squareup.haha.perflib.ArrayInstance;
import com.squareup.haha.perflib.ClassInstance;
import com.squareup.haha.perflib.ClassObj;
import com.squareup.haha.perflib.Heap;
import com.squareup.haha.perflib.Instance;
import com.squareup.haha.perflib.Snapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\Administrator\\Desktop\\dump.hprof");

        DataBuffer dataBuffer = new MemoryMappedFileBuffer(file);

        Snapshot snapshot = Snapshot.createSnapshot(dataBuffer);

        Map<String, List<AnaylizerResult>> resultMap = new HashMap<>();

        Collection<ClassObj> bitmapClasses = snapshot.findClasses("android.graphics.Bitmap");

        List<Heap> heaps = (List<Heap>) snapshot.getHeaps();

        for (Heap heap : heaps) {
            System.out.println("heapName: " + heap.getName());
//            anaylizerbuffer(resultMap, heap, bitmapClasses);
            for (ClassObj classObj : bitmapClasses) {
                List<Instance> bitmapClassHeapInstances = classObj.getHeapInstances(heap.getId());
                for (Instance bitmapInstance : bitmapClassHeapInstances) {

                    String md5 = HahaHelper.getMd5(bitmapInstance);

                    List<AnaylizerResult> resultList;

                    if (!resultMap.containsKey(md5)) {
                        resultList = new ArrayList<>();
                    } else {
                        resultList = resultMap.get(md5);
                    }

                    resultList.add(generateResult(bitmapInstance));
                    resultMap.put(md5, resultList);
                }
            }
        }

        if (resultMap.isEmpty()) {
            System.out.println("未发现bitmap对象");
        }

        dumpResult(resultMap);

    }

    private static void anaylizerbuffer(Map<String, List<AnaylizerResult>> resultMap, Heap heap, Collection<ClassObj> bitmapClasses) {
        for (ClassObj classObj : bitmapClasses) {
            List<Instance> bitmapClassHeapInstances = classObj.getHeapInstances(heap.getId());

            for (Instance bitmapInstance : bitmapClassHeapInstances) {

                if (bitmapInstance.getDistanceToGcRoot() == Integer.MAX_VALUE) {
                    continue;
                }

                ArrayInstance instance = HahaHelper.fieldValue(((ClassInstance) bitmapInstance).getValues(), "mBuffer");
                byte[] bytes = instance.asRawByteArray(0, instance.getLength());
                String md5 = HahaHelper.MD5(bytes);

                if (resultMap.containsKey(md5)) {
                    resultMap.get(md5).add(generateResult(instance));
                } else {
                    ArrayList<AnaylizerResult> resultList = new ArrayList<>();
                    resultList.add(generateResult(instance));
                    resultMap.put(md5, resultList);
                }
            }
        }


    }

    private static AnaylizerResult generateResult(Instance instance) {
        AnaylizerResult result = new AnaylizerResult();
        List<ClassInstance.FieldValue> classInstaceValues = ((ClassInstance) instance).getValues();

        ArrayInstance bitmapBuffer = HahaHelper.fieldValue(classInstaceValues, "mBuffer");
        int bitmapHeight = HahaHelper.fieldValue(((ClassInstance) instance).getValues(), "mHeight");
        int bitmapWidth = HahaHelper.fieldValue(((ClassInstance) instance).getValues(), "mWidth");

        result.setBufferHash(HahaHelper.MD5(bitmapBuffer.asRawByteArray(0, bitmapBuffer.getLength())));
        result.setClassInstance(bitmapBuffer.toString());
        result.setBufferSize(bitmapBuffer.getValues().length);
        result.setHeight(bitmapHeight);
        result.setWidth(bitmapWidth);
        result.setInstance(instance);
        return result;
    }

    private static void dumpResult(Map<String, List<AnaylizerResult>> resultMap) {
        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, List<AnaylizerResult>> entry : resultMap.entrySet()) {
            List<AnaylizerResult> anaylizerResultList = entry.getValue();
            if (anaylizerResultList.size() >= 2) {
                String hash = null;
                int width = 0;
                int hegiht = 0;
                int size = 0;
                jsonObject.put("duplcateCount", anaylizerResultList.size());
                JSONArray jsonArray = new JSONArray();
                for (AnaylizerResult result : anaylizerResultList) {
                    Instance instance = result.getInstance();

                    while (instance.getNextInstanceToGcRoot() != null) {
                        jsonArray.add(instance.getNextInstanceToGcRoot());
                        System.out.println(instance.getNextInstanceToGcRoot());
                        instance = instance.getNextInstanceToGcRoot();
                    }
                    hash = result.getBufferHash();
                    width = result.getWidth();
                    hegiht = result.getHeight();
                    size = result.getBufferSize();
                }
                jsonObject.put("stacks", jsonArray);
                jsonObject.put("bufferHash", hash);
                jsonObject.put("width", width);
                jsonObject.put("hetight", hegiht);
                jsonObject.put("size", size);
            }
        }

        System.out.println(jsonObject.toString());
    }


}
