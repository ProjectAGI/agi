/*
 * Copyright (c) 2017.
 *
 * This file is part of Project AGI. <http://agi.io>
 *
 * Project AGI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Project AGI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Project AGI.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.agi.framework.coordination.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.agi.framework.Framework;
import io.agi.framework.persistence.PersistenceUtil;
import io.agi.framework.references.DataRefUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by dave on 2/04/16.
 */
public class HttpImportFileHandler implements HttpHandler {

    protected static final Logger _logger = LogManager.getLogger();

    public static final String CONTEXT = "/import-local";

    public static final String PARAMETER_FILE = "file";
    public static final String PARAMETER_TYPE = "type";

    public static final String TYPE_DATA = "data";
    public static final String TYPE_ENTITY = "entity";

    public static final String ZIP_FILE = "data.zip";

    public HttpImportFileHandler() {
    }

    @Override
    public void handle( HttpExchange t ) throws IOException {

        int status = 400;

        HashMap< String, String > responseMap = new HashMap<>();
        responseMap.put( "message", "Unable to handle request");

        try {
            String query = t.getRequestURI().getQuery();
            Map< String, String > queryParams = HttpUtil.GetQueryParams( query );

            if ( queryParams.containsKey( PARAMETER_FILE )
                    && queryParams.containsKey( PARAMETER_TYPE )) {

                String filepath = queryParams.get( PARAMETER_FILE ).trim();
                String type = queryParams.get( PARAMETER_TYPE ).trim();

                // Checks if the experiment data file exists
                File rawFile = new File(filepath);
                if ( !rawFile.exists() ) {
                    // Get prefix folder path without filename
                    String outputFolder = rawFile.getParent();

                    try {
                        Path zipFilePath = Paths.get(outputFolder, ZIP_FILE);

                        // Unzip the data file, retaining its original filename
                        ZipFile zipFile = new ZipFile(zipFilePath.toString());
                        zipFile.extractAll(outputFolder);
                    } catch( Exception e ) {
                        String message = "Failed to unzip data file.";
                        _logger.error( message );
                        _logger.error( e.toString(), e );
                    }
                }

                responseMap.put( "filepath", filepath );

                boolean success = false;
                if ( type.equals( TYPE_ENTITY ) ) {
                    success = PersistenceUtil.ReadEntities( filepath );
                    responseMap.put( "message", "Success" );
                    responseMap.put( "type", type );
                }
                else if ( type.equals( TYPE_DATA ) ) {
                    success = DataRefUtil.ReadData( filepath );
                    responseMap.put( "message", "Success" );
                    responseMap.put( "type", type );
                }
                else {
                    String message = "ERROR: unrecognised type";
                    responseMap.put( "message", message );
                    _logger.error( message );
                }

                if ( success ) {
                    status = 200;
                }
            }
            else {
                String message = "ERROR: You must specify parameters: 'import-file' and 'type'";
                responseMap.put( "message", message );
                _logger.error( message );
            }

        }
        catch( Exception e ) {
            String message = "Unable to import-local entities/data.";
            _logger.error( message );
            _logger.error( e.toString(), e );

            responseMap.put( "message", message );
        }

        String response = new Gson().toJson( responseMap );
        HttpUtil.SendResponse( t, status, response );
    }
}