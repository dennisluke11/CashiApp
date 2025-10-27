//
//  CashiApp.swift
//  iosApp
//
//  Created for Cashi Mobile App
//

import SwiftUI
import shared

@main
struct CashiApp: App {
    init() {
        // Initialize Koin for iOS
        initKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
    
    func initKoin() {
        // Start Koin with shared module
        let koinApplication = startKoinIos()
        _koin = NSObject(value: koinApplication)
    }
    
    @StateObject var koin = KoinIosKt.koin
}

// Global Koin instance
var koin: KoinIosKt? = nil

