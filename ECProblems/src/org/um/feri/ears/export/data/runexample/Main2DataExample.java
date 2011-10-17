/**
 * Insert data
 * <p>
 * 
 * @author Matej Crepinsek
 * @version 1
 * 
 *          <h3>License</h3>
 * 
 *          Copyright (c) 2011 by Matej Crepinsek. <br>
 *          All rights reserved. <br>
 * 
 *          <p>
 *          Redistribution and use in source and binary forms, with or without
 *          modification, are permitted provided that the following conditions
 *          are met:
 *          <ul>
 *          <li>Redistributions of source code must retain the above copyright
 *          notice, this list of conditions and the following disclaimer.
 *          <li>Redistributions in binary form must reproduce the above
 *          copyright notice, this list of conditions and the following
 *          disclaimer in the documentation and/or other materials provided with
 *          the distribution.
 *          <li>Neither the name of the copyright owners, their employers, nor
 *          the names of its contributors may be used to endorse or promote
 *          products derived from this software without specific prior written
 *          permission.
 *          </ul>
 *          <p>
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *          LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *          FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *          COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *          INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *          BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *          LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *          CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *          ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *          POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.um.feri.ears.export.data.runexample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

import net.sourceforge.jswarm_pso.SwarmAlgorithm;

import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.PlayerAlgorithm;
import org.um.feri.ears.algorithms.PlayerAlgorithmExport;
import org.um.feri.ears.algorithms.es.ES1p1sAlgorithm;
import org.um.feri.ears.algorithms.random.RandomWalkAlgorithm;
import org.um.feri.ears.algorithms.tlbo.TLBOAlgorithm;
import org.um.feri.ears.benchmark.RatingBenchmark;
import org.um.feri.ears.benchmark.RatingSUOPm;
import org.um.feri.ears.export.data.BenchmarkRunArena;
import org.um.feri.ears.export.data.StatP2PList;
import org.um.feri.ears.export.data.StatP2TaskList;
import org.um.feri.ears.export.data.StatPlayer2Player;
import org.um.feri.ears.export.data.StatPlayer2Task;
import org.um.feri.ears.export.data.WinnLossDraw;
import org.um.feri.ears.rating.Player;
import org.um.feri.ears.rating.Rating;
import org.um.feri.ears.rating.ResultArena;
import org.um.feri.ears.util.Util;

import com.erciyes.karaboga.bee.BeeColonyAlgorithm;
import com.google.gson.Gson;
import com.um.feri.brest.de.DEAlgorithm;

/**
 * @author Administrator
 * 
 */
public class Main2DataExample {
    public static void main(String[] args) {
        Util.rnd.setSeed(System.currentTimeMillis());
        long stTime = System.currentTimeMillis();
        RatingBenchmark.debugPrint = true; // prints one on one results
        BenchmarkRunArena data = new BenchmarkRunArena();
        ArrayList<Algorithm> players = new ArrayList<Algorithm>();
        players.add(new RandomWalkAlgorithm());
        // players.add(new RandomWalkAMAlgorithm());
        players.add(new ES1p1sAlgorithm());
        players.add(new SwarmAlgorithm());
        players.add(new BeeColonyAlgorithm());
        players.add(new TLBOAlgorithm());
        for (int k=1;k<11;k++)
         players.add(new DEAlgorithm(k,20));
        players.add(new DEAlgorithm(DEAlgorithm.JDE_rand_1_bin, 20));

        ResultArena ra = new ResultArena(100);
        RatingSUOPm suopm = new RatingSUOPm();
        ArrayList<PlayerAlgorithmExport> listAll = new ArrayList<PlayerAlgorithmExport>();
        PlayerAlgorithmExport tmp;
        Rating tmpr;
        for (Algorithm al : players) {
            // ra.addPlayer(al.getID(), 1500, 350, 0.06,0,0,0);
            tmpr = new Rating(1500, 350, 0.06); // TODO Read from file
            tmp = new PlayerAlgorithmExport(al, tmpr, 0, 0, 0);
            listAll.add(tmp);
            ra.addPlayer(tmp);
            suopm.registerAlgorithm(al);
        }
        suopm.run(ra, 10);
        ra.recalcRangs();
        Collections.sort(listAll, new Player.RatingComparator());
        int i = 0;
        ArrayList<String> keys = new ArrayList<String>();
        StatP2PList spl = new StatP2PList(data.ID);
        StatP2TaskList sptl = new StatP2TaskList(data.ID);
        StatPlayer2Player p2pTmp;
        data.players = new org.um.feri.ears.export.data.Player[listAll.size()];
        for (PlayerAlgorithmExport p: listAll) {
            System.out.println(p);  
            data.players[i++] = p.getExportPlayer();
            Set<String> play = p.wldPlayers.keySet();
            for (String alid : play) {
                if (!keys.contains(alid + p.getPlayerId())) //get info from A or B
                    if (!keys.contains(p.getPlayerId() + alid)) {
                        p2pTmp = new StatPlayer2Player();
                        p2pTmp.idPlayerOne = p.getPlayerId();
                        p2pTmp.idPlayerTwo = alid;
                        p2pTmp.data = new WinnLossDraw();
                        org.um.feri.ears.rating.WinLossDraw wl = p.wldPlayers.get(alid);
                        p2pTmp.data.d = wl.getDraw();
                        p2pTmp.data.w = wl.getWin();
                        p2pTmp.data.l = wl.getLoss();
                        spl.list.add(p2pTmp);
                        keys.add(alid + p.getPlayerId());
                    }
            }
            Set<String> problist = p.wldProblems.keySet();
            StatPlayer2Task sptTmp;
            for (String probid: problist) {
                sptTmp = new StatPlayer2Task();
                org.um.feri.ears.rating.WinLossDraw wl = p.wldProblems.get(probid);
                sptTmp.taskID = probid;
                sptTmp.stat = new WinnLossDraw();;
                sptTmp.stat.d = wl.getDraw();
                sptTmp.stat.w = wl.getWin();
                sptTmp.stat.l = wl.getLoss();
                sptl.list.add(sptTmp);
            }
        }
        long endTime = System.currentTimeMillis();
        data.duration = endTime - stTime;
        data.milisecDate = endTime;

        Gson gson = new Gson();
        String jsonRepresentation = gson.toJson(data, BenchmarkRunArena.class);
        String jsonP2P = gson.toJson(spl, StatP2PList.class);
        String jsonP2T = gson.toJson(sptl, StatP2TaskList.class);
        // String jsonRepresentation = gson.toJson(p2);
        System.out.println(jsonRepresentation);
        System.out.println(jsonP2P);
        System.out.println(jsonP2T);
    }

}