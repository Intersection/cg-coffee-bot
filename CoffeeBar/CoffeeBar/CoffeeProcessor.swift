//
//  CoffeeProcessor.swift
//  CoffeeBar
//
//  Created by Tim Mattison on 7/24/15.
//  Copyright (c) 2015 controlgroup. All rights reserved.
//

import Foundation

class CoffeeProcessor {
    func pollCoffeeUrl(endpointUrl: String?) -> AnyObject? {
        if endpointUrl == nil {
            // TODO: Do something clever here
            return nil
        }
        
        if let endpointNSURL = NSURL(string: endpointUrl!) {
            var error: NSError?
            let json = NSString(contentsOfURL: endpointNSURL, encoding: NSUTF8StringEncoding, error: &error)
            
            if let error = error {
                // TODO: Do something clever here
                println("Error: \(error)")
            } else {
                // Process the JSON
                return processJson(json!)
            }
        } else {
            // TODO: Do something clever here
            println("Couldn't create endpointNSURL")
        }
        
        return nil
    }
    
    private func processJson(json: NSString) -> AnyObject? {
        var parseError: NSError?
        let parsedObject: AnyObject? = NSJSONSerialization.JSONObjectWithData(json.dataUsingEncoding(NSUTF8StringEncoding)!,
            options: NSJSONReadingOptions.AllowFragments,
            error:&parseError)
        
        return parsedObject
    }
}