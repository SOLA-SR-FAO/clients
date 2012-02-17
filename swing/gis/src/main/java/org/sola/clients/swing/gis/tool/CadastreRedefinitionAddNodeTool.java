/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.Envelope2D;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.CadastreObjectNodeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionObjectLayer;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.clients.swing.gis.to.CadastreObjectNodeExtraTO;
import org.sola.common.MappingManager;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTO;

/**
 *
 * @author Elton Manoku
 */
public class CadastreRedefinitionAddNodeTool extends CadastreRedefinitionAbstractNodeTool {

    private String toolName = "add-node";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_TOOLTIP_ADD_NODE).getMessage();

    public CadastreRedefinitionAddNodeTool(PojoDataAccess dataAccess,
            CadastreRedefinitionNodeLayer cadastreObjectNodeModifiedLayer,
            CadastreRedefinitionObjectLayer cadastreObjectModifiedLayer) {
        super(dataAccess, cadastreObjectNodeModifiedLayer, cadastreObjectModifiedLayer);
        this.setToolName(toolName);
        this.setToolTip(toolTip);
    }

    @Override
    protected void onRectangleFinished(Envelope2D env) {

        CadastreObjectNodeBean nodeBean = this.addNodeFromServer(env);
        if (nodeBean == null){
            return;
        }
        SimpleFeature nodeFeature = 
                this.cadastreObjectNodeModifiedLayer.getFeatureCollection().getFeature(
                    nodeBean.getId());
        List<String> cadastreObjectTargetIds = new ArrayList<String>();
        for(CadastreObjectBean coBean: nodeBean.getCadastreObjectList()){
            cadastreObjectTargetIds.add(coBean.getId());
        }
        this.insertNode(nodeFeature, cadastreObjectTargetIds);
        if (!this.manipulateNode(nodeFeature)){
            this.removeNode(nodeFeature);
        }
    }

    @Override
    protected CadastreObjectNodeBean getNodeFromServer(Envelope2D env) {
        CadastreObjectNodeTO nodeTO =  
                this.dataAccess.getCadastreService().getCadastreObjectNodePotential(
                env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY(),
                this.getMapControl().getSrid());
        if (nodeTO == null) {
            return null;
        }
        CadastreObjectNodeBean nodeBean = MappingManager.getMapper().map(
                new CadastreObjectNodeExtraTO(nodeTO), CadastreObjectNodeBean.class);
        return nodeBean;
    }

    private void insertNode(SimpleFeature nodeFeature, List<String> cadastreObjectTargetIds) {
        Geometry nodeFeatureGeom = (Geometry) nodeFeature.getDefaultGeometry();
        Coordinate coordinate = nodeFeatureGeom.getCoordinate();
        for(String cadastreObjectTargetId:cadastreObjectTargetIds){
            SimpleFeature cadastreObjectFeature= 
                    this.cadastreObjectModifiedLayer.getFeatureCollection().getFeature(
                    cadastreObjectTargetId);
            if (cadastreObjectFeature == null){
                continue;
            }
            Polygon cadastreObjectGeom = (Polygon) cadastreObjectFeature.getDefaultGeometry();
            LinearRing exteriorRing = this.insertCoordinateInRing(
                    cadastreObjectGeom.getExteriorRing(), coordinate);

            LinearRing[] interiorRings = new LinearRing[cadastreObjectGeom.getNumInteriorRing()];
            for (
                    int interiorRingIndex = 0; 
                    interiorRingIndex < interiorRings.length; 
                    interiorRingIndex++) {
                interiorRings[interiorRingIndex] = this.insertCoordinateInRing(
                        cadastreObjectGeom.getInteriorRingN(interiorRingIndex), coordinate);
            }

            cadastreObjectGeom = 
                    this.cadastreObjectModifiedLayer.getGeometryFactory().createPolygon(
                    exteriorRing, interiorRings);
            cadastreObjectFeature.setDefaultGeometry(cadastreObjectGeom);
            cadastreObjectGeom.geometryChanged();
        }
        this.getMapControl().refresh();
    }

    private LinearRing insertCoordinateInRing(
            LineString target, Coordinate coordinate) {
        LocationIndexedLine line = new LocationIndexedLine(target);
        LinearLocation linearLocation = line.project(coordinate);

        int newCoordIndex = linearLocation.getSegmentIndex() + 1;

        com.vividsolutions.jts.geom.CoordinateList coordinates =
                new CoordinateList(target.getCoordinates(), false);

        coordinates.add(newCoordIndex,
                new Coordinate(coordinate.x, coordinate.y), true);

        coordinates.closeRing();

        return this.cadastreObjectModifiedLayer.getGeometryFactory().createLinearRing(
                coordinates.toCoordinateArray());

    }
}