//
//  AppDelegate.swift
//  CoffeeBar
//
//  Created by Tim Mattison on 7/24/15.
//  Copyright (c) 2015 controlgroup. All rights reserved.
//

import Cocoa

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {
    let configPlistName = "Config"
    let apiEndpointConfigKeyName = "API endpoint"
    let refreshIntervalInSeconds = 15.0
    let statusItem = NSStatusBar.systemStatusBar().statusItemWithLength(-2)
    var refreshTimer:NSTimer?
    var endpointUrl:String?
    
    func applicationDidFinishLaunching(aNotification: NSNotification) {
        loadButtonIcon()
        createMenu()
        populateEndpointUrl()
        createRefreshTimer()
    }
    
    private func loadButtonIcon() {
        if let button = statusItem.button {
            button.image = NSImage(named: "StatusBarButtonImage")
        }
    }
    
    private func createMenu() {
        let menu = NSMenu()
        
        menu.addItem(NSMenuItem(title: "Quit CoffeeBar", action: Selector("terminate:"), keyEquivalent: "q"))
        
        statusItem.menu = menu
    }
    
    private func populateEndpointUrl() {
        if let path = NSBundle.mainBundle().pathForResource(configPlistName, ofType: "plist") {
            if let dict = NSDictionary(contentsOfFile: path) as? Dictionary<String, AnyObject> {
                // TODO: Check if this is "CHANGEME" and remind the user to change it
                endpointUrl = dict[apiEndpointConfigKeyName] as? String
            }
        }
    }
    
    private func createRefreshTimer() {
        refreshTimer = NSTimer(timeInterval: refreshIntervalInSeconds, target: self, selector: "refreshTimerFired", userInfo: nil, repeats: true)
        
        if refreshTimer != nil {
            NSRunLoop.currentRunLoop().addTimer(refreshTimer!, forMode: NSRunLoopCommonModes)
        }
    }
    
    func refreshTimerFired() {
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

