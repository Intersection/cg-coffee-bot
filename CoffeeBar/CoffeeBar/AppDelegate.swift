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
        refreshTimer = NSTimer(timeInterval: 0.5, target: self, selector: "refreshTimerFired", userInfo: nil, repeats: true)
        
        if refreshTimer != nil {
            NSRunLoop.currentRunLoop().addTimer(refreshTimer!, forMode: NSRunLoopCommonModes)
        }
    }
    
    func refreshTimerFired() {
        // TODO: JSON processing here
        println("Timer fired")
    }
}

