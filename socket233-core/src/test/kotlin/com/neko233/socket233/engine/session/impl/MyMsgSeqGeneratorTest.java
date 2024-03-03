//package com.neko233.networkengine.engine.session.impl;
//
//import com.neko233.networkengine.core.engine.msgSequence.impl.MyMsgSeqGenerator;
//import com.neko233.skilltree.commons.core.utils.KvTemplate233;
//import org.junit.jupiter.api.Test;
//
//import java.util.concurrent.ThreadLocalRandom;
//
///**
// * @author LuoHaoJun on 2023-06-01
// **/
//public class MyMsgSeqGeneratorTest {
//
//    @Test
//    public void nextMsgSequenceId() {
//        //		for (long l = (long)Double.MAX_VALUE; l <= Long.MAX_VALUE; l++) {
////			double d = l;
////			long _l = (long) d;
////			if (_l != l) {
////				System.out.printf("%s\t%s\t%s\n", d, l, _l);
////			}
////		}
//
//        long seed = ThreadLocalRandom.current().nextLong();
//        System.out.println("seed:" + seed);
//        System.out.println("mask1:" + MyMsgSeqGenerator.MASK);
//
//        MyMsgSeqGenerator generator = new MyMsgSeqGenerator(-1109831585);
//        for (int i = 0; i < 10; i++) {
////            System.out.println(generator.nextMsgSequenceId(null));
//
//            System.out.println(KvTemplate233.builder("seed = ${seed}, seqId = ${seqId}")
//                    .put("seed", generator.getSeed())
//                    .put("seqId", generator.nextMsgSequenceId())
//                    .build());
//        }
//    }
//}