//
//  CoffeeProcessor.swift
//  CoffeeBar
//
//  Created by Tim Mattison on 7/24/15.
//  Copyright (c) 2015 controlgroup. All rights reserved.
//

import Foundation

class CoffeeProcessor {
    func pollCoffeeUrl(endpointUrl: String?) {
        if endpointUrl == nil {
            // TODO: Do something clever here
            return
        }
        
        if let endpointNSURL = NSURL(string: endpointUrl!) {
            var error: NSError?
            let json = NSString(contentsOfURL: endpointNSURL, encoding: NSUTF8StringEncoding, error: &error) as? String
            
            if let error = error {
                // TODO: Do something clever here
                println("Error: \(error)")
            } else {
                // Process the JSON
                processJson(json!)
            }
        } else {
            // TODO: Do something clever here
            println("Couldn't create endpointNSURL")
        }
    }
    
    private func processJson(json: String) {
        // TODO: Process the JSON here
        println("\(json)")
    }
}